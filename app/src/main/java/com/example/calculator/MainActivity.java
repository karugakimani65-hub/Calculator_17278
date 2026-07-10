package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;
    private double firstValue = Double.NaN;
    private double secondValue;
    private char currentOperator = '\0';
    private DecimalFormat decimalFormat;
    private boolean isNewNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        decimalFormat = new DecimalFormat("#.##########");
        textViewResult = findViewById(R.id.textViewResult);

        setNumberListeners();
        setOperatorListeners();
    }

    private void setNumberListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener listener = v -> {
            Button b = (Button) v;
            String buttonText = b.getText().toString();
            String currentText = textViewResult.getText().toString();

            if (isNewNumber || currentText.equals("0")) {
                if (buttonText.equals(".")) {
                    textViewResult.setText("0.");
                } else {
                    textViewResult.setText(buttonText);
                }
                isNewNumber = false;
            } else {
                if (buttonText.equals(".") && currentText.contains(".")) {
                    return; // Avoid multiple dots
                }
                textViewResult.append(buttonText);
            }
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorListeners() {
        findViewById(R.id.btnAdd).setOnClickListener(v -> prepareOperation('+'));
        findViewById(R.id.btnSub).setOnClickListener(v -> prepareOperation('-'));
        findViewById(R.id.btnMul).setOnClickListener(v -> prepareOperation('*'));
        findViewById(R.id.btnDiv).setOnClickListener(v -> prepareOperation('/'));
        findViewById(R.id.btnPower).setOnClickListener(v -> prepareOperation('^'));
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            String currentText = textViewResult.getText().toString();
            if (!currentText.isEmpty() && !currentText.equals("Error")) {
                double val = Double.parseDouble(currentText) / 100;
                textViewResult.setText(decimalFormat.format(val));
                isNewNumber = true;
            }
        });

        findViewById(R.id.btnC).setOnClickListener(v -> {
            firstValue = Double.NaN;
            secondValue = Double.NaN;
            currentOperator = '\0';
            textViewResult.setText("0");
            isNewNumber = true;
        });

        findViewById(R.id.btnEqual).setOnClickListener(v -> compute());
    }

    private void prepareOperation(char operator) {
        String currentText = textViewResult.getText().toString();
        if (currentText.equals("Error")) return;

        if (!Double.isNaN(firstValue) && !isNewNumber) {
            compute();
        } else {
            firstValue = Double.parseDouble(currentText);
        }
        
        currentOperator = operator;
        isNewNumber = true;
    }

    private void compute() {
        if (!Double.isNaN(firstValue)) {
            String secondText = textViewResult.getText().toString();
            if (secondText.isEmpty() || secondText.equals("Error")) return;
            
            secondValue = Double.parseDouble(secondText);
            
            switch (currentOperator) {
                case '+': firstValue += secondValue; break;
                case '-': firstValue -= secondValue; break;
                case '*': firstValue *= secondValue; break;
                case '/':
                    if (secondValue != 0) {
                        firstValue /= secondValue;
                    } else {
                        textViewResult.setText("Error");
                        firstValue = Double.NaN;
                        currentOperator = '\0';
                        isNewNumber = true;
                        return;
                    }
                    break;
                case '^': firstValue = Math.pow(firstValue, secondValue); break;
            }
            textViewResult.setText(decimalFormat.format(firstValue));
            currentOperator = '\0';
            isNewNumber = true;
        }
    }
}