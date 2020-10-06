@GenericGenerator(
        name = Station.TABLE,
        //strategy = "enhanced-sequence",
        strategy = "ru.serge2nd.samples.hib.atmks.config.SequenceOrExistingIdGenerator",
        parameters = {
                @Parameter(name = "sequence_name", value = Station.TABLE + "_SEQ"),
                @Parameter(name = "initial_value", value = "1000"),
                @Parameter(name = "increment_size", value = "50") /* with pooled-lo optimizer */})
@GenericGenerator(
        name = Route.TABLE,
        //strategy = "enhanced-sequence",
        strategy = "ru.serge2nd.samples.hib.atmks.config.SequenceOrExistingIdGenerator",
        parameters = {
                @Parameter(name = "sequence_name", value = Route.TABLE + "_SEQ"),
                @Parameter(name = "initial_value", value = "1000"),
                @Parameter(name = "increment_size", value = "50") /* with pooled-lo optimizer */})
@GenericGenerator(
        name = Route.ROUTE_STOP_TABLE,
        strategy = "enhanced-sequence",
        parameters = {
                @Parameter(name = "sequence_name", value = Route.ROUTE_STOP_TABLE + "_SEQ"),
                @Parameter(name = "initial_value", value = "1000"),
                @Parameter(name = "increment_size", value = "50") /* with pooled-lo optimizer */})
package ru.serge2nd.samples.hib.atmks.data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
