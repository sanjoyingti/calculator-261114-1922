package com.aibuilt.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    var displayText by remember { mutableStateOf("0") }
    var errorState by remember { mutableStateOf(false) }

    fun updateDisplay(newText: String) {
        displayText = newText
        errorState = false
    }

    fun handleDigit(digit: Int) {
        if (errorState) {
            updateDisplay(digit.toString())
            return
        }
        if (displayText == "0" || displayText == "Error") {
            updateDisplay(digit.toString())
        } else {
            updateDisplay(displayText + digit)
        }
    }

    fun handleDot() {
        if (errorState) {
            updateDisplay("0.")
            return
        }
        if (!displayText.contains(".")) {
            updateDisplay(displayText + ".")
        }
    }

    fun handleClear() {
        CalculatorLogic.reset()
        updateDisplay("0")
    }

    fun handleBackspace() {
        if (errorState) {
            CalculatorLogic.reset()
            updateDisplay("0")
            return
        }
        if (displayText.length > 1) {
            updateDisplay(displayText.dropLast(1))
        } else {
            updateDisplay("0")
        }
    }

    fun handleOperation(selectedOp: CalculatorLogic.Operation) {
        if (errorState) return
        val currentOperand = try {
            displayText.toDouble()
        } catch (e: NumberFormatException) {
            return
        }

        CalculatorLogic.selectOperation(currentOperand, selectedOp)
    }

    fun handleEquals() {
        if (errorState) return
        val secondOperand = try {
            displayText.toDouble()
        } catch (e: NumberFormatException) {
            errorState = true
            updateDisplay("Error")
            return
        }

        val result = CalculatorLogic.calculate(firstOperand = null, secondOperand = secondOperand, op = CalculatorLogic.Operation.Add)
        val formatted = CalculatorLogic.formatResult(result)
        CalculatorLogic.reset()
        updateDisplay(formatted)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        DisplayArea(
            text = displayText,
            isError = errorState
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperationButton("7", onClick = { handleDigit(7) })
                OperationButton("8", onClick = { handleDigit(8) })
                OperationButton("9", onClick = { handleDigit(9) })
                OperatorButton("÷", onClick = { handleOperation(CalculatorLogic.Operation.Divide) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperationButton("4", onClick = { handleDigit(4) })
                OperationButton("5", onClick = { handleDigit(5) })
                OperationButton("6", onClick = { handleDigit(6) })
                OperatorButton("×", onClick = { handleOperation(CalculatorLogic.Operation.Multiply) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperationButton("1", onClick = { handleDigit(1) })
                OperationButton("2", onClick = { handleDigit(2) })
                OperationButton("3", onClick = { handleDigit(3) })
                OperatorButton("-", onClick = { handleOperation(CalculatorLogic.Operation.Subtract) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperationButton("C", onClick = { handleClear() })
                OperationButton("0", onClick = { handleDigit(0) })
                OperationButton(".", onClick = { handleDot() })
                OperationButton(
                    "⌫",
                    onClick = { handleBackspace() },
                    isUtilityButton = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperatorButton("+", onClick = { handleOperation(CalculatorLogic.Operation.Add) })
                EqualButton("=") { handleEquals() }
            }
        }
    }
}

@Composable
private fun DisplayArea(text: String, isError: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            color = if (isError) Color(0xFFEF9A9A) else MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isError) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun OperationButton(
    text: String,
    onClick: () -> Unit,
    isUtilityButton: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(72.dp)
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isUtilityButton) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isUtilityButton) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OperatorButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(72.dp)
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EqualButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(72.dp)
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Holds all the pure math/logic for the calculator.
 * Keeps the UI file small and makes the calculation rules easy to read.
 */
object CalculatorLogic {
    var currentFirstOperand: Double? = null
    var currentOperation: Operation? = null

    fun reset() {
        currentFirstOperand = null
        currentOperation = null
    }

    fun selectOperation(firstOperand: Double, op: Operation) {
        currentFirstOperand = firstOperand
        currentOperation = op
    }

    fun calculate(firstOperand: Double?, secondOperand: Double, op: Operation): Double {
        if (firstOperand != null && currentOperation != null) {
            return when (currentOperation) {
                Operation.Add -> firstOperand + secondOperand
                Operation.Subtract -> firstOperand - secondOperand
                Operation.Multiply -> firstOperand * secondOperand
                Operation.Divide -> {
                    if (secondOperand == 0.0) {
                        throw ArithmeticException("Division by zero")
                    }
                    firstOperand / secondOperand
                }
                else -> secondOperand
            }
        }
        return secondOperand
    }

    fun formatResult(result: Double): String {
        return when {
            result.isInfinite() || result.isNaN() -> "Error"
            result == result.toLong().toDouble() -> result.toLong().toString()
            else -> {
                val formatted = String.format(java.util.Locale.US, "%.10g", result)
                formatted.replaceFirst(Regex("e\\+?"), "e").replaceFirst(Regex("e-"), "e-")
            }
        }
    }

    enum class Operation {
        Add,
        Subtract,
        Multiply,
        Divide
    }
}
