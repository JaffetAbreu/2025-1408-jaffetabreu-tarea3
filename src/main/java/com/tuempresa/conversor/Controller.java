package com.tuempresa.conversor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.*;

public class Controller {

    @FXML
    private TextField inputField, resultField;

    @FXML
    private ComboBox<String> fromUnit, toUnit;

    @FXML
    private TextArea historyArea;

    private final Deque<String> history = new ArrayDeque<>();

    private final Map<String, Map<String, Double>> conversionRates = new HashMap<>();

    @FXML
    public void initialize() {
        // Validación numérica con TextFormatter
        StringConverter<Double> converter = new DoubleStringConverter();
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, change -> {
            if (change.getControlNewText().matches("-?\\d*(\\.\\d*)?")) return change;
            return null;
        });
        inputField.setTextFormatter(textFormatter);

        // Unidades disponibles
        List<String> units = List.of(
                "Metro", "Centímetro", "Pulgada", "Pie", "Yarda",
                "Kilogramo", "Gramo", "Libra", "Onza",
                "Celsius", "Fahrenheit", "Kelvin"
        );
        fromUnit.getItems().addAll(units);
        toUnit.getItems().addAll(units);

        resultField.setEditable(false);
        historyArea.setEditable(false);
    }

    @FXML
    private void convert() {
        String from = fromUnit.getValue();
        String to = toUnit.getValue();

        if (from == null || to == null) {
            showAlert("Debe seleccionar ambas unidades.");
            return;
        }

        if (from.equals(to)) {
            showAlert("Las unidades deben ser diferentes.");
            return;
        }

        double input;
        try {
            input = Double.parseDouble(inputField.getText());
        } catch (NumberFormatException e) {
            showAlert("Ingrese un número válido.");
            return;
        }

        double result;
        try {
            result = convertUnits(from, to, input);
        } catch (Exception e) {
            showAlert("Conversión no soportada.");
            return;
        }

        resultField.setText(String.format("%.4f", result));

        String log = String.format("%.2f %s → %.2f %s", input, from, result, to);
        addToHistory(log);
    }

    private double convertUnits(String from, String to, double value) {
        // Longitud
        Map<String, Double> length = Map.of(
                "Metro", 1.0,
                "Centímetro", 0.01,
                "Pulgada", 0.0254,
                "Pie", 0.3048,
                "Yarda", 0.9144
        );

        // Peso
        Map<String, Double> weight = Map.of(
                "Kilogramo", 1.0,
                "Gramo", 0.001,
                "Libra", 0.453592,
                "Onza", 0.0283495
        );

        if (length.containsKey(from) && length.containsKey(to)) {
            return value * (length.get(from) / length.get(to));
        }

        if (weight.containsKey(from) && weight.containsKey(to)) {
            return value * (weight.get(from) / weight.get(to));
        }

        // Temperatura
        return convertTemperature(from, to, value);
    }

    private double convertTemperature(String from, String to, double value) {
        if (from.equals("Celsius")) {
            return switch (to) {
                case "Fahrenheit" -> value * 9 / 5 + 32;
                case "Kelvin" -> value + 273.15;
                default -> throw new IllegalArgumentException();
            };
        } else if (from.equals("Fahrenheit")) {
            return switch (to) {
                case "Celsius" -> (value - 32) * 5 / 9;
                case "Kelvin" -> (value - 32) * 5 / 9 + 273.15;
                default -> throw new IllegalArgumentException();
            };
        } else if (from.equals("Kelvin")) {
            return switch (to) {
                case "Celsius" -> value - 273.15;
                case "Fahrenheit" -> (value - 273.15) * 9 / 5 + 32;
                default -> throw new IllegalArgumentException();
            };
        }

        throw new IllegalArgumentException();
    }

    @FXML
    private void clear() {
        inputField.clear();
        resultField.clear();
        fromUnit.setValue(null);
        toUnit.setValue(null);
    }

    private void addToHistory(String entry) {
        if (history.size() == 10) {
            history.removeFirst();
        }
        history.addLast(entry);
        StringBuilder sb = new StringBuilder();
        for (String line : history) {
            sb.append(line).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Conversión");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
