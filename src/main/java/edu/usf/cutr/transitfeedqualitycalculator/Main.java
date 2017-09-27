/*
 * Copyright (C) 2017 University of South Florida (sjbarbeau@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usf.cutr.transitfeedqualitycalculator;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    /**
     * Downloads and evaluates all GTFS-realtime feeds from TransitFeeds.com and outputs to the provided directory
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        TransitFeedQualityCalculator calculator = new TransitFeedQualityCalculator(Paths.get("feeds"));
        calculator.calculate();
    }
}
