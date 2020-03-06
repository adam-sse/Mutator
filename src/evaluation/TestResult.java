package evaluation;

public enum TestResult {

    PASS("passed"),
    
    TEST_FAILED("failed tests"),
    
    TIMEOUT("timed-out"),
    
    COMPILATION_FAILED("failed compilation"),
    
    ERROR("error"),
    
    ;
    
    private String msg;
    
    private TestResult(String msg) {
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return msg;
    }
    
}
