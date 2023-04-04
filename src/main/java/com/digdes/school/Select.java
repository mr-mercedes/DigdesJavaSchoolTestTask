package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.digdes.school.Table.TABLE;

public class Select {

    private final Util util = new Util();
    public Select() {
    }

    protected List<Map<String, Object>> select(List<String> tokens) {
        List<Map<String, Object>> table;
        if (!tokens.contains("WHERE")) {
            table = selectWithoutWhere();
        } else {
            table = selectWithWhere(tokens);
        }
        return table;
    }

    private List<Map<String, Object>> selectWithWhere(List<String> tokens) {
        List<List<Object>> actionKeys = util.extractActionKey(tokens);
        List<Map<String, Object>> selectTable = new ArrayList<>();

        if (!tokens.contains("AND") && !tokens.contains("OR")) {
            simpleSelect(actionKeys, selectTable);
        } else if (tokens.contains("AND")) {
            andSelect(actionKeys, selectTable);
        } else if (tokens.contains("OR")) {
            orSelect(actionKeys, selectTable);
        }
        return selectTable;
    }

    private void orSelect(List<List<Object>> actionKeys, List<Map<String, Object>> selectTable) {
        for (List<Object> actionKey : actionKeys) {
            if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                for (Map<String, Object> row : TABLE) {
                    if (util.compareRow(row, actionKey)) {
                        selectTable.add(row);
                    }
                }
            }
        }
    }

    private void andSelect(List<List<Object>> actionKeys, List<Map<String, Object>> selectTable) {
        for (Map<String, Object> row : TABLE) {
            for (List<Object> actionKey : actionKeys) {
                if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                    if (util.compareRow(row, actionKey) && !selectTable.contains(row)) {
                        selectTable.add(row);
                    } else if (util.compareRow(row, actionKey) || selectTable.contains(row)) {
                        if (selectTable.contains(row) && util.compareRow(row, actionKey)) {
                            selectTable.remove(row);
                            selectTable.add(row);
                        } else {
                            selectTable.remove(row);
                        }
                    }
                }
            }
        }
    }

    private void simpleSelect(List<List<Object>> actionKeys, List<Map<String, Object>> selectTable) {
        for (Map<String, Object> row : TABLE) {
            for (List<Object> actionKey : actionKeys) {
                if (util.compareRow(row, actionKey)) {
                    selectTable.add(row);
                }
            }
        }
    }

    private List<Map<String, Object>> selectWithoutWhere() {
        return TABLE;
    }
}
