package org.example.suites;

import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectPackages;

@Suite
@SelectPackages("org.example.unit")
public class UnitTestSuite {
}
