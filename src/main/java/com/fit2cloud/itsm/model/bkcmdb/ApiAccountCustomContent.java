package com.fit2cloud.itsm.model.bkcmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ApiAccountCustomContent {
    private boolean syncEnable;
    private List<String> levels;
}
