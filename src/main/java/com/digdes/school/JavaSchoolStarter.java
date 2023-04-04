package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaSchoolStarter {
    private final List<Map<String, Object>> table = new ArrayList<>();

    private Long id;
    private String lastname;
    private Double cost;
    private Long age;
    private Boolean active;

    public JavaSchoolStarter() {
    }


    public List<Map<String, Object>> execute(String request) throws Exception {
        List<String> tokens = getTokens(request);
        String operation = tokens.get(0);
        switch (operation) {
            case "INSERT" -> insert(tokens);
            case "UPDATE" -> update(tokens);
            case "DELETE" -> delete(tokens);
            case "SELECT" -> {
                return select(tokens);
            }
            default -> throw new RuntimeException("Unknown operation " + operation);
        }
        return table;
    }

    private void insert(List<String> tokens) {
        if (!tokens.contains("VALUES"))
            throw new RuntimeException("Error SQL syntax: INSERT operation expected VALUES");
        createRow(tokens);
        Map<String, Object> newRow = fillRow();
        table.add(newRow);
    }

    private void createRow(List<String> tokens) {
        clearOldData();
        updateCol(tokens);
    }

    private Map<String, Object> fillRow() {
        Map<String, Object> newRow = new HashMap<>();
        newRow.put("id", id);
        newRow.put("lastName", lastname);
        newRow.put("age", age);
        newRow.put("cost", cost);
        newRow.put("active", active);
        return newRow;
    }

    private void update(List<String> tokens) {
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
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            updateRow(tokens, row);
            Map<String, Object> updateRow = fillRow();
            table.remove(index);
            table.add(index, updateRow);
            index++;
        }
    }

    private void updateRow(List<String> tokens, Map<String, Object> row) {
        clearOldData();
        extractOldCol(row);
        updateCol(tokens);
    }

    private void updateWithWhere(List<String> tokens) {
        List<List<Object>> updateKeys = extractActionKey(tokens);

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
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            for (List<Object> keys : updateKeys) {
                if (!keys.get(0).equals("AND") && !keys.get(0).equals("OR")) {
                    Boolean compareRow = compareRow(row, keys);
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
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            for (List<Object> keys : updateKeys) {
                if (!keys.get(0).equals("AND") && !keys.get(0).equals("OR")) {
                    if (compareRow(row, keys) && !flag) {
                        flag = true;
                    } else if (compareRow(row, keys) && flag) {
                        flag = moveUpdate(tokens, index, row);
                        index++;
                    } else if (!compareRow(row, keys)) {
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
        Map<String, Object> updateRow = fillRow();
        table.remove(index);
        table.add(index, updateRow);
        return flag;
    }

    private void simpleUpdate(List<List<Object>> updateKeys, List<String> tokens) {
        int index = 0;
        if (!tokens.contains("AND") && !tokens.contains("OR")) {
            while (table.size() > index) {
                Map<String, Object> row = table.get(index);
                for (List<Object> keys : updateKeys) {
                    Boolean compareRow = compareRow(row, keys);
                    if (compareRow) {
                        updateRow(tokens, row);
                        Map<String, Object> updateRow = fillRow();
                        table.remove(index);
                        table.add(index, updateRow);
                    }
                }
                index++;
            }
        }
    }

    private Boolean compareRow(Map<String, Object> row, List<Object> item) {
        String key = String.valueOf(item.get(0));
        String operator = String.valueOf(item.get(1));
        Object value = item.get(2);
        String rowKey;
        Object rowValue;
        for (Map.Entry<String, Object> map : row.entrySet()) {
            rowKey = map.getKey();
            rowValue = map.getValue();
            if (key.equalsIgnoreCase(rowKey)) {
                if (!compareOperators(operator)) {
                    if (value.equals(rowValue) || (rowValue == null && value == "null")) {
                        return true;
                    }
                }
                return customerEquals(key, rowValue, value, operator);
            }
        }
        return false;
    }

    private Boolean compareOperators(String actionOperator) {
        switch (actionOperator) {
            case "<=", ">=", "!=", "<", ">", "LIKE", "ILIKE" -> {
                return true;
            }
            case "=" -> {
                return false;
            }
            default ->
                    throw new RuntimeException("Error SQL syntax: expected operation to compare, but has: " + actionOperator);
        }
    }

    private Boolean customerEquals(String key, Object rowValue, Object compareValue, String operator) { //поправить операторы
        boolean compare = false;

        switch (key.toLowerCase()) {
            case "lastname" -> {
                switch (operator) {
                    case "=", "!=" -> compare = String.valueOf(rowValue).equalsIgnoreCase(String.valueOf(compareValue));
                    case "LIKE","ILIKE" -> compare = compareLike(rowValue, compareValue, operator);
                }
            }
            case "id", "age" -> {
                rowValue = rowValue != null ? (Long) rowValue : 0L;
                compareValue = compareValue != null ? (Long) compareValue : 0L;
                switch (operator) {
                    case "=" -> compare = (rowValue).equals(compareValue);
                    case "<=" -> compare = (Long) rowValue <= (Long) compareValue;
                    case ">=" -> compare = (Long) rowValue >= (Long) compareValue;
                    case "!=" -> compare = !Objects.equals(rowValue, compareValue);
                    case "<" -> compare = (Long) rowValue < (Long) compareValue;
                    case ">" -> compare = (Long) rowValue > (Long) compareValue;
                }
            }
            case "cost" -> {
                rowValue = rowValue != null ? (Double) rowValue : 0.;
                compareValue = compareValue != null ? (Double) compareValue : 0.;
                switch (operator) {
                    case "=" -> compare = (rowValue).equals(compareValue);
                    case "<=" -> compare = (Double) rowValue <= (Double) compareValue;
                    case ">=" -> compare = (Double) rowValue >= (Double) compareValue;
                    case "!=" -> compare = !Objects.equals(rowValue, compareValue);
                    case "<" -> compare = (Double) rowValue < (Double) compareValue;
                    case ">" -> compare = (Double) rowValue > (Double) compareValue;
                }
            }
            case "active" -> {
                if (compareValue.equals("null") || rowValue.equals("null")) {
                    return compareValue.equals("null") && rowValue == "null";
                }
                compare = Boolean.compare((Boolean) rowValue, (Boolean) compareValue) == 0;
            }
        }
        return compare;
    }

    private boolean compareLike(Object rowValue, Object compareValue, String operator) {
        String rowVal;
        String compVal;

        if (operator.equalsIgnoreCase("LIKE")) {
            rowVal = String.valueOf(rowValue);
            compVal = String.valueOf(compareValue);
        } else {
            rowVal = String.valueOf(rowValue).toLowerCase();
            compVal = String.valueOf(compareValue).toLowerCase();
        }

        char firstSymbol = compVal.charAt(0);
        char lastSymbol = compVal.charAt(compVal.length() - 1);
        String substring = compVal.substring(1, compVal.length() - 1);

        if (!compVal.contains("%")){
            return rowVal.equals(compVal);
        } else {
            if (firstSymbol == '%' && lastSymbol == '%'){
                String regex = "[A-Za-zА-Яа-я]*"+ substring +"[A-Za-zА-Яа-я]*";
                return patternCompare(rowVal, regex);
            } else if (firstSymbol == '%') {
                String regex = "[A-Za-zА-Яа-я]*"+ substring;
                return patternCompare(rowVal, regex);
            } else if (lastSymbol == '%') {
                String regex = substring + "[A-Za-zА-Яа-я]*";
                return patternCompare(rowVal, regex);
            }
        }
        return false;
    }

    private static boolean patternCompare(String rowVal, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rowVal);
        return matcher.matches();
    }


    private List<List<Object>> extractActionKey(List<String> tokens) {
        List<List<Object>> extractKeys = new ArrayList<>();
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equalsIgnoreCase("WHERE")) {
                getKeys(tokens, extractKeys, i, token);
            }
            if (token.equalsIgnoreCase("AND")) {
                getKeys(tokens, extractKeys, i, token);
            }
            if (token.equalsIgnoreCase("OR")) {
                getKeys(tokens, extractKeys, i, token);
            }
        }
        return extractKeys;
    }

    private void getKeys(List<String> tokens, List<List<Object>> keys, int index, String keyWord) {
        if (!keyWord.equalsIgnoreCase("WHERE")) keys.add(List.of(keyWord));
        String actionKey;
        Object actionValue = null;
        String actionOperator;
        actionKey = tokens.get(index + 1);
        switch (actionKey.toLowerCase()) {
            case "lastname" -> actionValue = tokens.get(index + 3);
            case "id", "age" -> actionValue = Long.parseLong(tokens.get(index + 3));
            case "cost" -> actionValue = Double.parseDouble(tokens.get(index + 3));
            case "active" ->
                    actionValue = !Objects.equals(tokens.get(index + 3), "null") ? Boolean.valueOf(tokens.get(index + 3)) : "null";
        }
        actionOperator = tokens.get(index + 2);
        keys.add(List.of(actionKey, actionOperator, Objects.requireNonNull(actionValue)));
    }

    private void extractOldCol(Map<String, Object> row) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key.toLowerCase()) {
                case "lastname" -> lastname = (String) value;
                case "id" -> id = (Long) value;
                case "age" -> age = (Long) value;
                case "cost" -> cost = (Double) value;
                case "active" -> active = (Boolean) value;
            }
        }
    }

    private void updateCol(List<String> tokens) {
        for (int i = 2; i < tokens.size(); i++) {
            String item = tokens.get(i);
            if (item.equalsIgnoreCase("WHERE")) break;
            switch (item.toLowerCase()) {
                case "lastname" -> lastname = tokens.get(i + 2);
                case "id" -> id = Long.parseLong(tokens.get(i + 2));
                case "age" -> age = Long.parseLong(tokens.get(i + 2));
                case "cost" -> cost = Double.parseDouble(tokens.get(i + 2));
                case "active" -> active = Boolean.valueOf(tokens.get(i + 2));
            }
        }
    }

    private void delete(List<String> tokens) {
        if (!tokens.contains("WHERE")) {
            deleteWithoutWhere();
        } else {
            deleteWithWhere(tokens);
        }
    }

    private void deleteWithWhere(List<String> tokens) {
        List<List<Object>> actionKeys = extractActionKey(tokens);

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
                table.removeIf(row -> compareRow(row, actionKey));
            }
        }
    }

    private void andDelete(List<List<Object>> actionKeys) {
        boolean flag = false;
        int index = 0;
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            for (List<Object> actionKey : actionKeys) {
                if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                    if (compareRow(row, actionKey) && !flag) {
                        flag = true;
                    } else if (compareRow(row, actionKey) && flag) {
                        table.remove(row);
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
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            for (List<Object> actionKey : actionKeys) {
                if (compareRow(row, actionKey)) {
                    table.remove(row);
                } else {
                    index++;
                }
            }
        }
    }

    private void deleteWithoutWhere() {
        table.clear();
    }

    private List<Map<String, Object>> select(List<String> tokens) {
        List<Map<String, Object>> table;
        if (!tokens.contains("WHERE")) {
            table = selectWithoutWhere();
        } else {
            table = selectWithWhere(tokens);
        }
        return table;
    }

    private List<Map<String, Object>> selectWithWhere(List<String> tokens) {
        List<List<Object>> actionKeys = extractActionKey(tokens);
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
                for (Map<String, Object> row : table) {
                    if (compareRow(row, actionKey)) {
                        selectTable.add(row);
                    }
                }
            }
        }
    }

    private void andSelect(List<List<Object>> actionKeys, List<Map<String, Object>> selectTable) {
        for (Map<String, Object> row : table) {
            for (List<Object> actionKey : actionKeys) {
                if (!actionKey.get(0).equals("AND") && !actionKey.get(0).equals("OR")) {
                    if (compareRow(row, actionKey) && !selectTable.contains(row)) {
                        selectTable.add(row);
                    } else if (compareRow(row, actionKey) || selectTable.contains(row)) {
                        if (selectTable.contains(row) && compareRow(row, actionKey)) {
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
        for (Map<String, Object> row : table) {
            for (List<Object> actionKey : actionKeys) {
                if (compareRow(row, actionKey)) {
                    selectTable.add(row);
                }
            }
        }
    }

    private List<Map<String, Object>> selectWithoutWhere() {
        return table;
    }

    private List<String> getTokens(String request) {
        checkColumnName(request);
        String regex = "([A-Za-z_.0-9]+)|([А-Яа-я_.0-9]+)|([0-9])|(=|!=|<=|>=|>|<)|(%[A-Za-zА-Яа-я]*.|[A-Za-zА-Яа-я]*%)";

        return getItems(request, regex);
    }

    private String convertOperators(String token) {
        switch (token.toUpperCase()) {
            case "INSERT", "UPDATE", "DELETE", "SELECT", "VALUE", "WHERE", "LIKE", "ILIKE", "OR", "AND" -> {
                return token.toUpperCase();
            }
            default -> {
                return token;
            }
        }
    }

    private void clearOldData() {
        id = null;
        lastname = null;
        age = null;
        cost = null;
        active = null;
    }

    private void checkColumnName(String request) {
        List<String> trueColumnNames = Arrays.asList("id", "cost", "age", "active", "lastname");
        List<String> tokensColumnNames = extractColumnNames(request);

        for (String token : tokensColumnNames) {
            String tokenColumnName = token.substring(1, token.length() - 1);
            if (!trueColumnNames.contains(tokenColumnName.toLowerCase())) {
                throw new RuntimeException("Error SQL syntax: unknown column name: " + tokenColumnName);
            }
        }
    }

    private List<String> extractColumnNames(String request) {
        String regex = "('[A-Za-z']+)";
        return getItems(request, regex);
    }

//    private List<String> extractLikeItems(String request) {
//        String regex = "(%[A-Za-zА-Яа-я]*.|[A-Za-zА-Яа-я]*%)";
//        return getItems(request, regex);
//    }

    private List<String> getItems(String request, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);

        List<String> tokensColumnNames = new ArrayList<>();
        while (matcher.find()) {
            tokensColumnNames.add(convertOperators(matcher.group()));
        }
        return tokensColumnNames;
    }

}
