package org.example.suites;

import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectPackages;

@Suite
@SelectPackages("org.example.integration")
public class IntegrationTestSuite {
}
