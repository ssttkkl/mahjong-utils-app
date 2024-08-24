/*
 * Copyright (c) 1995, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package io.ssttkkl.mahjongutils.app.utils.url

/**
 * Utility class for HTML form encoding. This class contains static methods
 * for converting a String to the <CODE>application/x-www-form-urlencoded</CODE> MIME
 * format. For more information about HTML form encoding, consult the HTML
 * <A HREF="http://www.w3.org/TR/html4/">specification</A>.
 *
 *
 *
 * When encoding a String, the following rules apply:
 *
 *
 *  * The alphanumeric characters &quot;`a`&quot; through
 * &quot;`z`&quot;, &quot;`A`&quot; through
 * &quot;`Z`&quot; and &quot;`0`&quot;
 * through &quot;`9`&quot; remain the same.
 *  * The special characters &quot;`.`&quot;,
 * &quot;`-`&quot;, &quot;`*`&quot;, and
 * &quot;`_`&quot; remain the same.
 *  * The space character &quot; &nbsp; &quot; is
 * converted into a plus sign &quot;`+`&quot;.
 *  * All other characters are unsafe and are first converted into
 * one or more bytes using some encoding scheme. Then each byte is
 * represented by the 3-character string
 * &quot;*`%xy`*&quot;, where *xy* is the
 * two-digit hexadecimal representation of the byte.
 * The recommended encoding scheme to use is UTF-8. However,
 * for compatibility reasons, if an encoding is not specified,
 * then the default encoding of the platform is used.
 *
 *
 *
 *
 * For example using UTF-8 as the encoding scheme the string &quot;The
 * string &#252;@foo-bar&quot; would get converted to
 * &quot;The+string+%C3%BC%40foo-bar&quot; because in UTF-8 the character
 * &#252; is encoded as two bytes C3 (hex) and BC (hex), and the
 * character @ is encoded as one byte 40 (hex).
 *
 * @author  Herb Jellinek
 * @since   1.0
 */
object URLEncoder {

    /* The list of characters that are not encoded has been
     * determined as follows:
     *
     * RFC 2396 states:
     * -----
     * Data characters that are allowed in a URI but do not have a
     * reserved purpose are called unreserved.  These include upper
     * and lower case letters, decimal digits, and a limited set of
     * punctuation marks and symbols.
     *
     * unreserved  = alphanum | mark
     *
     * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
     *
     * Unreserved characters can be escaped without changing the
     * semantics of the URI, but this should not be done unless the
     * URI is being used in a context that does not allow the
     * unescaped character to appear.
     * -----
     *
     * It appears that both Netscape and Internet Explorer escape
     * all special characters from this list with the exception
     * of "-", "_", ".", "*". While it is not clear why they are
     * escaping the other characters, perhaps it is safest to
     * assume that there might be contexts in which the others
     * are unsafe if not escaped. Therefore, we will use the same
     * list. It is also noteworthy that this is consistent with
     * O'Reilly's "HTML: The Definitive Guide" (page 164).
     *
     * As a last note, Intenet Explorer does not encode the "@"
     * character which is clearly not unreserved according to the
     * RFC. We are being consistent with the RFC in this matter,
     * as is Netscape.
     *
     */
    var dontNeedEncoding: MutableList<Byte> = ArrayList<Byte>(255).apply {
        repeat(255) { add(0) }

        var i: Int = 'a'.code
        while (i <= 'z'.code) {
            set(i, 1)
            i++
        }
        i = 'A'.code
        while (i <= 'Z'.code) {
            set(i, 1)
            i++
        }
        i = '0'.code
        while (i <= '9'.code) {
            set(i, 1)
            i++
        }
        set(' '.code, 1) /* encoding a space to a + is done
                                    * in the encode() method */
        set('-'.code, 1)
        set('_'.code, 1)
        set('.'.code, 1)
        set('*'.code, 1)

    }
    const val caseDiff = 'a'.code - 'A'.code

    fun encodeToUtf8(s: String): String {
        var needToChange = false
        val out: StringBuilder = StringBuilder(s.length)
        val charArrayWriter: StringBuilder = StringBuilder()
        var i = 0
        while (i < s.length) {
            var c = s[i].code
            //System.out.println("Examining character: " + c);
            if (dontNeedEncoding.get(c) > 0) {
                if (c == ' '.code) {
                    c = '+'.code
                    needToChange = true
                }
                //System.out.println("Storing: " + c);
                out.append(c.toChar())
                i++
            } else {
                // convert to external encoding before hex conversion
                do {
                    charArrayWriter.append(c)
                    /*
                     * If this character represents the start of a Unicode
                     * surrogate pair, then pass in two characters. It's not
                     * clear what should be done if a byte reserved in the
                     * surrogate pairs range occurs outside of a legal
                     * surrogate pair. For now, just treat it as if it were
                     * any other character.
                     */if (c >= 0xD800 && c <= 0xDBFF) {
                        /*
                          System.out.println(Integer.toHexString(c)
                          + " is high surrogate");
                        */
                        if (i + 1 < s.length) {
                            val d = s[i + 1].code
                            /*
                              System.out.println("\tExamining "
                              + Integer.toHexString(d));
                            */if (d >= 0xDC00 && d <= 0xDFFF) {
                                /*
                                  System.out.println("\t"
                                  + Integer.toHexString(d)
                                  + " is low surrogate");
                                */
                                charArrayWriter.append(d)
                                i++
                            }
                        }
                    }
                    i++
                } while (i < s.length && dontNeedEncoding.get(
                        s[i].code.also { c = it }) == (0).toByte()
                )
                val str = charArrayWriter.toString()
                val ba: ByteArray = str.encodeToByteArray()
                for (j in ba.indices) {
                    out.append('%')
                    var ch: Char = charForDigit(ba[j].toInt() shr 4 and 0xF, 16)
                    // converting to use uppercase letter as part of
                    // the hex value if ch is a letter.
                    if (ch.isLetter()) {
                        ch = Char(ch.code - caseDiff)
                    }
                    out.append(ch)
                    ch = charForDigit(ba[j].toInt() and 0xF, 16)
                    if (ch.isLetter()) {
                        ch = Char(ch.code - caseDiff)
                    }
                    out.append(ch)
                }
                charArrayWriter.clear()
                needToChange = true
            }
        }
        return if (needToChange) out.toString() else s
    }

    // END Android-changed: Reimplement methods natively on top of ICU4C.
    private fun charForDigit(digit: Int, radix: Int): Char {
        if (digit >= radix || digit < 0) {
            return '\u0000'
        }
        if (radix < 2 || radix > 36) {
            return '\u0000'
        }
        return if (digit < 10) {
            ('0'.code + digit).toChar()
        } else ('a'.code - 10 + digit).toChar()
    }
}
