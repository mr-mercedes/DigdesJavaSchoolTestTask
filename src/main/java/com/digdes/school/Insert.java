package com.digdes.school;


import java.util.List;
import java.util.Map;

import static com.digdes.school.Table.TABLE;

public class Insert {
    private final Util util = new Util();
    public Insert() {
    }
    protected void insert(List<String> tokens) {
        if (!tokens.contains("VALUES"))
            throw new RuntimeException("Error SQL syntax: INSERT operation expected VALUES");
        createRow(tokens);
        Map<String, Object> newRow = util.fillRow();
        TABLE.add(newRow);
    }

    private void createRow(List<String> tokens) {
        util.clearOldData();
        util.updateCol(tokens);
    }
}
