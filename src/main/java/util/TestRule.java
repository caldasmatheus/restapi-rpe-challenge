package util;

import com.aventstack.extentreports.testng.listener.ExtentITestListenerAdapter;
import org.testng.annotations.Listeners;

@Listeners(ExtentITestListenerAdapter.class)
public abstract class TestRule {
}
