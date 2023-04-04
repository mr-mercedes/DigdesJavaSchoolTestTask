package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {
    public Util() {
    }

    protected List<String> getTokens(String request) {
        checkColumnName(request);
        String regex = "([A-Za-z_.0-9]+)|([А-Яа-я_.0-9]+)|([0-9])|(=|!=|<=|>=|>|<)|(%[A-Za-zА-Яа-я]*.|[A-Za-zА-Яа-я]*%)";

        return getItems(request, regex);
    }

    protected void updateCol(List<String> tokens) {
        for (int i = 2; i < tokens.size(); i++) {
            String item = tokens.get(i);
            if (item.equalsIgnoreCase("WHERE")) break;
            switch (item.toLowerCase()) {
                case "lastname" -> Table.setLastname(tokens.get(i + 2));
                case "id" -> Table.setId(Long.parseLong(tokens.get(i + 2)));
                case "age" -> Table.setAge(Long.parseLong(tokens.get(i + 2)));
                case "cost" -> Table.setCost(Double.parseDouble(tokens.get(i + 2)));
                case "active" -> Table.setActive(Boolean.valueOf(tokens.get(i + 2)));
            }
        }
    }

    protected void clearOldData() {
        Table.setId(null);
        Table.setLastname(null);
        Table.setAge(null);
        Table.setCost(null);
        Table.setActive(null);
    }

    protected Boolean compareRow(Map<String, Object> row, List<Object> item) {
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

    protected Map<String, Object> fillRow() {
        Map<String, Object> newRow = new HashMap<>();
        newRow.put("id", Table.getId());
        newRow.put("lastName", Table.getLastname());
        newRow.put("age", Table.getAge());
        newRow.put("cost", Table.getCost());
        newRow.put("active", Table.getActive());
        return newRow;
    }

    protected List<List<Object>> extractActionKey(List<String> tokens) {
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

    private List<String> getItems(String request, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);

        List<String> tokensColumnNames = new ArrayList<>();
        while (matcher.find()) {
            tokensColumnNames.add(convertOperators(matcher.group()));
        }
        return tokensColumnNames;
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

    private List<String> extractColumnNames(String request) {
        String regex = "('[A-Za-z']+)";
        return getItems(request, regex);
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

    private Boolean customerEquals(String key, Object rowValue, Object compareValue, String operator) { //поправить операторы
        boolean compare = false;

        switch (key.toLowerCase()) {
            case "lastname" -> {
                switch (operator) {
                    case "=", "!=" -> compare = String.valueOf(rowValue).equalsIgnoreCase(String.valueOf(compareValue));
                    case "LIKE", "ILIKE" -> compare = compareLike(rowValue, compareValue, operator);
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

        if (!compVal.contains("%")) {
            return rowVal.equals(compVal);
        } else {
            if (firstSymbol == '%' && lastSymbol == '%') {
                String regex = "[A-Za-zА-Яа-я]*" + substring + "[A-Za-zА-Яа-я]*";
                return patternCompare(rowVal, regex);
            } else if (firstSymbol == '%') {
                String regex = "[A-Za-zА-Яа-я]*" + substring;
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
}
