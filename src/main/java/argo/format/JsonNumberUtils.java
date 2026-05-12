/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Helper methods for converting JSON number <code>String</code>s into Java numeric objects.
 */
public final class JsonNumberUtils {

    /**
     * Helper method for converting JSON number <code>String</code>s into <code>BigDecimal</code>s.
     *
     * @param jsonNumberString a <code>String</code> representation of a JSON number.
     * @return a <code>BigDecimal</code> representing the given JSON number <code>String</code>.
     * @throws NumberFormatException if the given <code>String</code> is not a valid JSON number.
     */
    public static BigDecimal asBigDecimal(final String jsonNumberString) {
        return jsonNumberString == null ? null : new BigDecimal(jsonNumberString);
    }

    /**
     * Helper method for converting JSON number <code>String</code>s into <code>BigInteger</code>s.
     *
     * @param jsonNumberString a <code>String</code> representation of an integer JSON number.
     * @return a <code>BigInteger</code> representing the given JSON number <code>String</code>.
     * @throws NumberFormatException if the given <code>String</code> is not a valid JSON number or is not an integer.
     */
    public static BigInteger asBigInteger(final String jsonNumberString) {
        try {
            return jsonNumberString == null ? null : asBigDecimal(jsonNumberString).toBigIntegerExact();
        } catch (ArithmeticException e) {
            throw new NumberFormatException("Given String [" + jsonNumberString + "] was non-integer");
        }
    }

    /**
     * Helper method for converting JSON number <code>String</code>s into <code>Double</code>s.  Note that use of this
     * method is risky as java <code>Double</code>s have limited precision, whereas JSON numbers have unlimited
     * precision, i.e. converting a JSON number to a <code>Double</code> may result is a loss of accuracy.
     *
     * @param jsonNumberString a <code>String</code> representation of an integer JSON number.
     * @return a <code>Double</code> representing the given JSON number <code>String</code>.
     * @throws NumberFormatException if the given <code>String</code> is not a valid JSON number.
     */
    public static Double asDouble(final String jsonNumberString) {
        return jsonNumberString == null ? null : asBigDecimal(jsonNumberString).doubleValue();
    }

    /**
     * Helper method for converting JSON number <code>String</code>s into <code>Integer</code>s.  Note that use of this
     * method is risky as java <code>Integer</code>s have limited precision (between <code>Integer.MIN_VALUE</code> and
     * <code>Integer.MAX_VALUE</code>) whereas JSON numbers have unlimited precision.
     *
     * @param jsonNumberString a <code>String</code> representation of an integer JSON number.
     * @return a <code>Integer</code> representing the given JSON number <code>String</code>.
     * @throws NumberFormatException if the given <code>String</code> is not a valid JSON number or is not an integer.
     */
    public static Integer asInteger(final String jsonNumberString) {
        return jsonNumberString == null ? null : asBigInteger(jsonNumberString).intValue();
    }
}
