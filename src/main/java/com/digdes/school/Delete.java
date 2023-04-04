package com.digdes.school;

import java.util.List;
import java.util.Map;

import static com.digdes.school.Table.TABLE;

public class Delete {
    private final Util util = new Util();

    public Delete() {
    }

    protected void delete(List<String> tokens) {
        if (!tokens.contains("WHERE")) {
            deleteWithoutWhere();
        } else {
            deleteWithWhere(tokens);
        }
    }

    private void deleteWithWhere(List<String> tokens) {
        List<List<Object>> actionKeys = util.extractActionKey(tokens);

        if (!tokens.contains("AND") && !tokens.contains("OR")) {
            simpleDelete(actionKeys);
        } else if (tokens.contains("AND")) {
            andDelete(actionKeys);
        } else if (tokens.contains("OR")) {
            orDelete(actionKeys);
        }
    }

    private void orDelete(List<List<Object>> actionKeys) {
        for (List<Object> actionKey : actionKeys) {
            if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                TABLE.removeIf(row -> util.compareRow(row, actionKey));
            }
        }
    }

    private void andDelete(List<List<Object>> actionKeys) {
        boolean flag = false;
        int index = 0;
        while (TABLE.size() > index) {
            Map<String, Object> row = TABLE.get(index);
            for (List<Object> actionKey : actionKeys) {
                if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                    if (util.compareRow(row, actionKey) && !flag) {
                        flag = true;
                    } else if (util.compareRow(row, actionKey) && flag) {
                        TABLE.remove(row);
                        flag = false;
                    } else {
                        index++;
                        flag = false;
                    }
                }
            }
        }
    }

    private void simpleDelete(List<List<Object>> actionKeys) {
        int index = 0;
        while (TABLE.size() > index) {
            Map<String, Object> row = TABLE.get(index);
            for (List<Object> actionKey : actionKeys) {
                if (util.compareRow(row, actionKey)) {
                    TABLE.remove(row);
                } else {
                    index++;
                }
            }
        }
    }

    private void deleteWithoutWhere() {
        TABLE.clear();
    }
}
