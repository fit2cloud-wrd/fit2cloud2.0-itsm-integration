echo "清理旧文件 ..."
rm -rf *.tar.gz *.md5 extension

module_version="module.version"
property_file="src/main/resources/application.properties"
has_version=`grep "^$module_version=" $property_file | wc -l`
if [ "$has_version" -ne "0" ]; then
  sed -i '/^module\.version=/d' $property_file
fi
version=`awk '/<version>[^<]+<\/version>/{gsub(/<version>|<\/version>/,"",$1);print $1;exit;}' pom.xml`

has_eureka_enable_setting=`grep "^eureka.client.enabled=" $property_file | wc -l`
if [ "$has_eureka_enable_setting" -ne "0" ]; then
  sed -i '/^eureka.client.enabled=/d' $property_file
fi
echo "eureka.client.enabled=true" >> $property_file

echo "$module_version=$version" >> $property_file

docker_compose_path=docker-compose.yml

if [ "${internal}" == 'true' ];then
    echo "内网镜像"
    docker_compose_path=dev/docker-compose.yml
fi

sed -i "s/BRANCH/${branch}/" ${docker_compose_path}
image_url=`grep "image:" ${docker_compose_path} | awk -F "image: " '{print $NF}' | head -n 1`
image_name=`echo $image_url | awk -F"/" '{ print $3 }'`
image=`echo $image_name | awk -F":" '{ print $1 }'`

echo "编译工程源码 ..."
mvn clean package -U -Dmaven.test.skip=true

function export_image_package() {
  install_file=$image-$version.tar.gz

  echo "拉取扩展模块 $1 镜像 ..."
  docker pull --platform="linux/$1" "$image_url"
  if [[ "$1" == "arm64" ]]; then
    install_file=$image-arm64-$version.tar.gz
  fi

  echo "导出扩展模块 $1 镜像 ..."
  docker save -o extension/"$image_name".tar "$image_url"
  if [ -d conf ]; then
    echo "复制配置文件夹 ..."
    \cp -rp conf extension/
  fi

  if [ -d scripts ]; then
    echo "复制脚本文件夹 ..."
    \cp -rp scripts extension/
  fi
  \cp service.inf extension/
  \cp service.ico extension/
  \cp ${docker_compose_path} extension/
  #Mac的sed命令
  #sed -i "" '/^version=/d' service.inf
  #Linux的sed命令
  sed -i '/^version=/d' extension/service.inf
  echo "version=$build_version" >> extension/service.inf

  echo "制作扩展模块 $1 安装包 ..."
  tar zcvf "${install_file}" extension

  md5_file_name=${install_file}.md5
  md5sum "${install_file}" | awk -F" " '{print "md5: "$1}' > "${md5_file_name}"
}

function build_commit_logs() {
    echo "Branch:\t${branch}" > target/commits.txt
    git log -n 10 --pretty=format:"%h\t%an\t%ad\t%s" --date=iso >> target/commits.txt
    echo "构建commit镜像日志文件"
}

function build_image() {
  echo "构建扩展模块镜像 ..."
  if [[ "${tools}" == 'buildx' ]]; then
    docker buildx build -t $image_url --platform=linux/arm64,linux/amd64 . --push
  else
    docker build -t $image_url .
    if [[ "${action}" == 'push' ]]; then
        docker push $image_url
    fi
  fi
}
build_commit_logs
#构建镜像
build_image

if [ -d extension ]; then
  rm -rf extension
fi
mkdir extension

# AMD 打包
export_image_package amd64

# ARM 打包
export_image_package arm64

# 删除多架构镜像
rm -rf extension
