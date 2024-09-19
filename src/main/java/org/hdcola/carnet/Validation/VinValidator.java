package org.hdcola.carnet.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashMap;
import java.util.Map;

public class VinValidator implements ConstraintValidator<Vin, String> {
    @Override
    public boolean isValid(String vin, ConstraintValidatorContext constraintValidatorContext) {
        //general format of the vin, 17 characters I Q O are not allowed to avoid confusion with 1 and 0
        if (!vin.matches("[A-HJ-NPR-Z0-9]{17}")) {
            return false;
        }

        //EXPLANATION: Checksum calculation
        // The VIN is composed of 17 characters, each of which has a specific weight
        // A checksum is calculated based on the weights of each character, and should equal the mod 11 of the 9th character
        //if there is a mistake in the 9th character, the checksum will not be correct, and the VIN will be invalid

        // Weight values for each position in the VIN
        int[] weights = {8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};

        //
        Map<Character, Integer> transliterations = new HashMap<>();
        transliterations.put('A', 1);
        transliterations.put('B', 2);
        transliterations.put('C', 3);
        transliterations.put('D', 4);
        transliterations.put('E', 5);
        transliterations.put('F', 6);
        transliterations.put('G', 7);
        transliterations.put('H', 8);
        transliterations.put('J', 1);
        transliterations.put('K', 2);
        transliterations.put('L', 3);
        transliterations.put('M', 4);
        transliterations.put('N', 5);
        transliterations.put('P', 7);
        transliterations.put('R', 9);
        transliterations.put('S', 2);
        transliterations.put('T', 3);
        transliterations.put('U', 4);
        transliterations.put('V', 5);
        transliterations.put('W', 6);
        transliterations.put('X', 7);
        transliterations.put('Y', 8);
        transliterations.put('Z', 9);
        for (char c = '0'; c <= '9'; c++) {
            transliterations.put(c, c - '0'); // Maps '0'-'9' to their numeric values
        }

        // Calculate the checksum
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char currentChar = vin.charAt(i);
            sum += transliterations.get(currentChar) * weights[i];
        }

        int remainder = sum % 11;
        String checkDigit = remainder == 10 ? "X" : String.valueOf(remainder);

        // Validate the 9th character (check digit)
        return vin.substring(8, 9).equals(checkDigit);
    }

}
