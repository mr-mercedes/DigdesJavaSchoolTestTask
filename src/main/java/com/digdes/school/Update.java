package com.digdes.school;

import java.util.List;
import java.util.Map;

import static com.digdes.school.Table.TABLE;

public class Update {
    public Update() {
    }

    private final Util util = new Util();

    protected void update(List<String> tokens) {
        if (!tokens.contains("VALUES"))
            throw new RuntimeException("Error SQL syntax: UPDATE operation expected VALUES");
        if (!tokens.contains("WHERE")) {
            updateWithoutWhere(tokens);
        } else {
            updateWithWhere(tokens);
        }
    }

    private void updateWithoutWhere(List<String> tokens) {
        int index = 0;
        while (TABLE.size() > index) {
            Map<String, Object> row = TABLE.get(index);
            updateRow(tokens, row);
            Map<String, Object> updateRow = util.fillRow();
            TABLE.remove(index);
            TABLE.add(index, updateRow);
            index++;
        }
    }

    private void updateRow(List<String> tokens, Map<String, Object> row) {
        util.clearOldData();
        extractOldCol(row);
        util.updateCol(tokens);
    }

    private void updateWithWhere(List<String> tokens) {
        List<List<Object>> updateKeys = util.extractActionKey(tokens);

        if (!tokens.contains("AND") && !tokens.contains("OR")) {
            simpleUpdate(updateKeys, tokens);
        } else if (tokens.contains("AND")) {
            andUpdate(updateKeys, tokens);
        } else if (tokens.contains("OR")) {
            orUpdate(updateKeys, tokens);
        }
    }

    private void orUpdate(List<List<Object>> updateKeys, List<String> tokens) {
        boolean flag = false;
        int index = 0;
        while (TABLE.size() > index) {
            Map<String, Object> row = TABLE.get(index);
            for (List<Object> keys : updateKeys) {
                if (!keys.get(0).equals("AND") && !keys.get(0).equals("OR")) {
                    Boolean compareRow = util.compareRow(row, keys);
                    if (compareRow) {
                        flag = true;
                    }
                    if (compareRow && flag) {
                        flag = moveUpdate(tokens, index, row);
                    }
                }
            }
            index++;
        }
    }

    private void andUpdate(List<List<Object>> updateKeys, List<String> tokens) {
        boolean flag = false;
        int index = 0;
        while (TABLE.size() > index) {
            Map<String, Object> row = TABLE.get(index);
            for (List<Object> keys : updateKeys) {
                if (!keys.get(0).equals("AND") && !keys.get(0).equals("OR")) {
                    if (util.compareRow(row, keys) && !flag) {
                        flag = true;
                    } else if (util.compareRow(row, keys) && flag) {
                        flag = moveUpdate(tokens, index, row);
                        index++;
                    } else if (!util.compareRow(row, keys)) {
                        index++;
                        flag = false;
                        break;
                    } else {
                        index++;
                        flag = false;
                    }
                }
            }
        }
    }

    private boolean moveUpdate(List<String> tokens, int index, Map<String, Object> row) {
        boolean flag = false;
        updateRow(tokens, row);
        Map<String, Object> updateRow = util.fillRow();
        TABLE.remove(index);
        TABLE.add(index, updateRow);
        return flag;
    }

    private void simpleUpdate(List<List<Object>> updateKeys, List<String> tokens) {
        int index = 0;
        if (!tokens.contains("AND") && !tokens.contains("OR")) {
            while (TABLE.size() > index) {
                Map<String, Object> row = TABLE.get(index);
                for (List<Object> keys : updateKeys) {
                    Boolean compareRow = util.compareRow(row, keys);
                    if (compareRow) {
                        updateRow(tokens, row);
                        Map<String, Object> updateRow = util.fillRow();
                        TABLE.remove(index);
                        TABLE.add(index, updateRow);
                    }
                }
                index++;
            }
        }
    }

    private void extractOldCol(Map<String, Object> row) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key.toLowerCase()) {
                case "lastname" -> Table.setLastname((String) value);
                case "id" -> Table.setId((Long) value);
                case "age" -> Table.setAge((Long) value);
                case "cost" -> Table.setCost((Double) value);
                case "active" -> Table.setActive((Boolean) value);
            }
        }
    }
}
