/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
/*
 * @test
 * @bug 4052473 4052679 4055602 4066550 4067619 4068012 4068073 4070174 4070452
 *      4070178 4070450 4070695 4070725 4070795 4071003 4071183 4071782 4072013
 *      4072388 4072773 4075404 4084356 4087238 4092361 4094033 4094371 4098518
 *      4099810 4103218 4103220 4103861 4112136 4113638 4113654 4117054 4122468
 *      4122840 4139860 4156708 4175306 4215747 4209960 4290801 4900884 4942982
 *      4518811 4945388 4936845 4794068 4461740 4965260 4984277 4826794 5032580
 *      5102005 5074431 6182685 6208712 6277020 6245766 6351682 6386647 6379382
 *      6414459 6455680 6498742 6558863 6488119 6547501 6497154 6558856 6481177
 *      6379214 6485516 6486607 4225362 4494727 6533691 6531591 6531593 6570259
 *      6573250 6450945 6509039 6610748 6645268 6645405 6650730 6609737
 * @summary Verify locale data
 *
 */

/*
 *
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * Portions copyright (c) 2007 Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

/*    This test is a generalized test for verifying changes to the locale data.
 *    It is driven by an external file that specifies the particular pieces of locale
 *    data to check.  That file is in .properties file format: a series of key/value
 *    pairs delimited by newline characters, with the keys separated from the values
 *    by = signs.  The keys are similar in syntax to a Unix pathname, with keys at
 *    successive levels of containment in the resource-data hierarchy separated by
 *    slashes.  The file is in ISO 8859-1 encoding, with control characters and
 *    non-ASCII characters denoted with backslash-u escape sequences.  The program also allows
 *    blank lines and comment lines to be interspersed with the data.  Comment lines
 *    begin with '#'.
 *
 *    A data file for this test would look something like this:<pre>
 *        FormatData//MonthNames/0=January
 *        FormatData//MonthNames/1=February
 *        LocaleNames//US=United States
 *        LocaleNames//FR=France
 *        FormatData/fr_FR/MonthNames/0=janvier
 *        FormatData/fr_FR/MonthNames/1=f\u00e9vrier
 *        LocaleNames/fr_FR/US=\u00c9tats-Unis
 *        LocaleNames/fr_FR/FR=France</pre>
 *
 *    The command-line syntax of this test is
 *        <tt>java LocaleDataTest [-w] [{ -s | <filename> }]</tt>
 *
 *    This program always sends its results to standard output.   If -w is not specified,
 *    this program prints out only the differences between the data file and the actual
 *    resource data.  If -w is specified, the program prints out every entry, comment,
 *    and blank line from the data file.  Where there is a difference between the data
 *    file and the resource data, the data is the data from the resources.  This feature
 *    can be used to quickly generate a new data file.
 *
 *    The user can specify an optional filename or -s.  If the user specifies a filename,
 *    the program uses that file as the data file.  If the user specifies -s, the program
 *    reads its input from standard input rather than from a file.  If the user specifies
 *    neither, the program reads its input from a file called LocaleData in the same
 *    directory the program itself resides in.
 *
 *    The -nothrow option prevents the program from throwing an exception when it
 *    gets an error.  -w implies -nothrow.
 *
 *    Other command-line options can be specified, but are ignored.
 *
 *    It's important to note what this test will NOT test.  Certain changes to the locale
 *    data are meant to have certain effects on the internationalization frameworks.  For
 *    instance, we could ensure round-trip formatting/parsing integrity for the full
 *    date/time format of SimpleDateFormat by making sure that the full date and time
 *    patterns include sufficient data.  The test of this is not whether changes were
 *    made to the locale data; it's whether using this data gives round-trip integrity.
 *    Likewise, changing the currency patterns to use \u00a4 instead of local currency
 *    symbols isn't something that can be tested by this test; instead, you want to
 *    actually format currency values and make sure the proper currency symbol was used.
 *
 *    This test by itself doesn't do an exhaustive comparison of locale data.  It is
 *    possible to do this manually, however:  Use the GenerateKeyList tool to produce
 *    a complete list of keys for the two versions of the locales you want to compare,
 *    and then diff them.  This will flag additions and deletions.  Generate a data file
 *    for the base version of the data using the -w option and the output from
 *    GenerateKeyList, and then use the resultant file as the data file when you run
 *    this test against the new version of the data.
 */

import java.io.*;
import java.text.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.MissingResourceException;

public class LocaleDataTest
{
    public static void main(String[] args) throws Exception {

        // set up our flags and our input and output streams based on the
        // command-line arguments (exceptions generated here will propagate out
        // to the environment)
        BufferedReader in = null;
        PrintWriter out = null;
        boolean writeNewFile = false;
        boolean doThrow = true;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-w")) {
                writeNewFile = true;
                doThrow = false;
            }

            else if (args[i].equals("-nothrow"))
                doThrow = false;

            else if (args[i].equals("-s") && in == null)
                in = new BufferedReader(new EscapeReader(new InputStreamReader(System.in,
                                "ISO8859_1")));
            else if (!args[i].startsWith("-") && in == null)
                in = new BufferedReader(new EscapeReader(new InputStreamReader(new
                                FileInputStream(args[i]), "ISO8859_1")));
        }
        if (in == null) {
            File localeData = new File(System.getProperty("test.src", "."), "LocaleData");
            in = new BufferedReader(new EscapeReader(new InputStreamReader(new
                            FileInputStream(localeData), "ISO8859_1")));
        }
        out = new PrintWriter(new EscapeWriter(new OutputStreamWriter(System.out,
                        "ISO8859_1")), true);

        // perform the actual test
        int errorCount = doTest(in, out, writeNewFile);

        // write out the error count, and throw an exception out into the environment
        // if there were any errors
        if (errorCount != 0) {
            if (!writeNewFile)
                out.println("Test failed.  " + errorCount + " errors.");
            if (doThrow)
                throw new Exception("Test failed.  " + errorCount + " errors.");
        }
        else if (!writeNewFile)
            out.println("Test passed.");

        in.close();
        out.close();
    }

    static int doTest(BufferedReader in, PrintWriter out, boolean writeNewFile)
                    throws Exception {
        int errorCount = 0;

        String key = null;
        String expectedValue = null;
        String line = in.readLine();
        while (line != null) {
            if (line.startsWith("#") || line.length() == 0) {
                if (writeNewFile)
                    out.println(line);
            }

            else {
                int index  = line.indexOf("=");
                if (index == -1) {
                    key = line;
                    expectedValue = "";
                }
                else {
                    key = line.substring(0, index);
                    if (index + 1 == line.length())
                        expectedValue = "";
                    else
                        expectedValue = line.substring(index + 1);
                }
                if (!processLine(key, expectedValue, out, writeNewFile))
                    ++errorCount;
            }
            line = in.readLine();
        }
        return errorCount;
    }

    static boolean processLine(String key, String expectedValue, PrintWriter out,
                    boolean writeNewFile) throws Exception {
        String rbName, localeName, resTag, qualifier;
        String language = "", country = "", variant = "";
        int index, oldIndex;

        index = key.indexOf("/");
        if (index == -1 || index + 1 == key.length())
            throw new Exception("Malformed input file: no slashes in \"" + key + "\"");
        rbName = key.substring(0, index);

        oldIndex = index + 1;
        index = key.indexOf("/", oldIndex);
        if (index == -1 || index + 1 == key.length())
            throw new Exception("Malformed input file: \"" + key + "\" is missing locale name");
        localeName = key.substring(oldIndex, index);
        if (localeName.length() > 0) {
            language = localeName.substring(0, 2);
            if (localeName.length() > 3) {
                country = localeName.substring(3, 5);
                if (localeName.length() > 5)
                    variant = localeName.substring(6);
            }
        }

        oldIndex = index + 1;
        index = key.indexOf("/", oldIndex);
        if (index == -1)
            index = key.length();
        resTag = key.substring(oldIndex, index);

        if (index < key.length() - 1)
            qualifier = key.substring(index + 1);
        else
            qualifier = "";

        String retrievedValue = null;
        Object resource = null;
        try {
            String fullName = null;
            if (rbName.equals("CalendarData")
                    || rbName.equals("CurrencyNames")
                    || rbName.equals("LocaleNames")
                    || rbName.equals("TimeZoneNames")) {
                fullName = "sun.util.resources." + rbName;
            } else {
                fullName = "sun.text.resources." + rbName;
            }
            ResourceBundle bundle = ResourceBundle.getBundle(fullName,
                           new Locale(language, country, variant),
                           ResourceBundle.Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
            resource = bundle.getObject(resTag);
        }
        catch (MissingResourceException e) {
        }

        if (resource != null) {
            if (resource instanceof String) {
                retrievedValue = (String)resource;
            }
            else if (resource instanceof String[]) {
                int element = Integer.valueOf(qualifier).intValue();
                String[] stringList = (String[])resource;
                if (element >= 0 || element < stringList.length)
                    retrievedValue = stringList[element];
            }
            else if (resource instanceof String[][]) {
                String[][] stringArray = (String[][])resource;
                int slash = qualifier.indexOf("/");
                if (slash == -1) {
                    for (int i = 0; i < stringArray.length; i++) {
                        if (stringArray[i][0].equals(qualifier))
                            retrievedValue = stringArray[i][1];
                    }
                }
                else {
                    int row = Integer.valueOf(qualifier.substring(0, slash)).intValue();
                    int column = Integer.valueOf(qualifier.substring(slash + 1)).intValue();
                    if (row >= 0 || row < stringArray.length || column >= 0 || column <
                                    stringArray[row].length)
                        retrievedValue = stringArray[row][column];
                }
            }
        }

        if (retrievedValue == null || !retrievedValue.equals(expectedValue)) {
            if (retrievedValue == null)
                retrievedValue = "<MISSING!>";

            if (writeNewFile)
                out.println(key + "=" + retrievedValue);
            else {
                out.println("Mismatch in " + key + ":");
                out.println("  file = \"" + expectedValue + "\"");
                out.println("   jvm = \"" + retrievedValue + "\"");
            }
            return false;
        }
        else {
            if (writeNewFile)
                out.println(key + "=" + expectedValue);
        }
        return true;
    }
}

class EscapeReader extends FilterReader {
    public EscapeReader(Reader in) {
        super(in);
    }

    public int read() throws IOException {
        if (buffer != null) {
            String b = buffer.toString();
            int result = b.charAt(0);
            if (b.length() > 1)
                buffer = new StringBuffer(b.substring(1));
            else
                buffer = null;
            return result;
        }
        else {
            int result = super.read();
            if (result != '\\')
                return result;
            else {
                buffer = new StringBuffer();
                result = super.read();
                buffer.append((char)result);
                if (result == 'u') {
                    for (int i = 0; i < 4; i++) {
                        result = super.read();
                        if (result == -1)
                            break;
                        buffer.append((char)result);
                    }
                    String number = buffer.toString().substring(1);
                    result = Integer.parseInt(number, 16);
                    buffer = null;
                    return result;
                }
                return '\\';
            }
        }
    }

    public int read(char[] cbuf, int start, int len) throws IOException {
        int p = start;
        int end = start + len;
        int c = 0;
        while (c != -1 && p < end) {
            c = read();
            if (c != -1)
                cbuf[p++] = (char)c;
        }
        if (c == -1 && p == start)
            return -1;
        else
            return p - start;
    }

    private StringBuffer buffer = null;
}

class EscapeWriter extends FilterWriter {
    public EscapeWriter(Writer out) {
        super(out);
    }

    public void write(int c) throws IOException {
        if ((c >= ' ' && c <= '\u007e') || c == '\r' || c == '\n')
            super.write(c);
        else {
            super.write('\\');
            super.write('u');
            String number = Integer.toHexString(c);
            if (number.length() < 4)
                number = zeros.substring(0, 4 - number.length()) + number;
            super.write(number.charAt(0));
            super.write(number.charAt(1));
            super.write(number.charAt(2));
            super.write(number.charAt(3));
        }
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        int end = off + len;
        while (off < end)
            write(cbuf[off++]);
    }

    public void write(String str, int off, int len) throws IOException {
        int end = off + len;
        while (off < end)
            write(str.charAt(off++));
    }

    private static String zeros = "0000";
}