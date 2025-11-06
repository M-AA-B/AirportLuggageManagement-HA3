package com.bangs.luggage;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        EquipmentTest.class,
        StorageManagerTest.class,
        TaskTest.class,
        TaskManagerTest.class,
        LoggerTest.class,
        LogMetaTest.class,
        StreamSimTest.class,
        AppExceptionsTest.class
})
public class AllTests { }
