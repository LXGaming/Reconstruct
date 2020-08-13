/*
 * Copyright 2020 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.reconstruct.util.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.util.List;

@Plugin(name = "color", category = PatternConverter.CATEGORY)
@ConverterKeys({"color", "colour"})
@PerformanceSensitive("allocation")
public class ColorConverter extends LogEventPatternConverter {
    
    private static final Boolean ANSI_OVERRIDE = getOptionalBooleanProperty("terminal.ansi");
    private static final String ANSI_RESET = "\u001B[39;0m";
    private static final String ANSI_DEBUG = "\u001B[36;1m";
    private static final String ANSI_ERROR = "\u001B[31;1m";
    private static final String ANSI_WARN = "\u001B[33;1m";
    private final List<PatternFormatter> formatters;
    
    /**
     * Construct the converter.
     *
     * @param formatters The pattern formatters to generate the text to highlight
     */
    protected ColorConverter(List<PatternFormatter> formatters) {
        super("highlightError", null);
        this.formatters = formatters;
    }
    
    @Override
    public boolean handlesThrowable() {
        for (PatternFormatter formatter : formatters) {
            if (formatter.handlesThrowable()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (isAnsiSupported()) {
            Level level = event.getLevel();
            if (level.isMoreSpecificThan(Level.ERROR)) {
                format(event, toAppendTo, ANSI_ERROR);
                return;
            } else if (level.isMoreSpecificThan(Level.WARN)) {
                format(event, toAppendTo, ANSI_WARN);
                return;
            } else if (level.isLessSpecificThan(Level.DEBUG)) {
                format(event, toAppendTo, ANSI_DEBUG);
                return;
            }
        }
        
        formatters.forEach(formatter -> formatter.format(event, toAppendTo));
    }
    
    private void format(LogEvent event, StringBuilder toAppendTo, String style) {
        int start = toAppendTo.length();
        toAppendTo.append(style);
        int end = toAppendTo.length();
        
        formatters.forEach(formatter -> formatter.format(event, toAppendTo));
        
        int newEnd = toAppendTo.length();
        if (end == newEnd) {
            // No content so we don't need to append the ANSI escape code
            toAppendTo.setLength(start);
        } else {
            // Insert ANSI reset before new line to prevent JLine from inserting another one
            while (--newEnd >= 0) {
                char c = toAppendTo.charAt(newEnd);
                if (c != '\n' && c != '\r') {
                    break;
                }
            }
            
            toAppendTo.insert(newEnd + 1, ANSI_RESET);
        }
    }
    
    /**
     * Gets a new instance of the {@link ColorConverter} with the
     * specified options.
     *
     * @param config  The current configuration
     * @param options The pattern options
     * @return The new instance
     */
    public static ColorConverter newInstance(Configuration config, String[] options) {
        if (options.length != 1) {
            LOGGER.error("Incorrect number of options on highlightError. Expected 1 received " + options.length);
            return null;
        }
        
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on highlightError");
            return null;
        }
        
        if (System.getProperty("log4j.skipJansi") == null && isAnsiSupported()) {
            System.setProperty("log4j.skipJansi", "false");
        }
        
        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        return new ColorConverter(formatters);
    }
    
    public static boolean isAnsiSupported() {
        if (ANSI_OVERRIDE != null) {
            return ANSI_OVERRIDE;
        }
        
        return System.console() != null;
    }
    
    private static Boolean getOptionalBooleanProperty(String name) {
        String value = PropertiesUtil.getProperties().getStringProperty(name);
        if (value == null) {
            return null;
        } else if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (value.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else {
            LOGGER.warn("Invalid value for boolean input property '{}': {}", name, value);
            return null;
        }
    }
}