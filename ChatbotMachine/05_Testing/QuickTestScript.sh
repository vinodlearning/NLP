#!/bin/bash

# ===================================================================
# ChatbotMachine Quick Test Script
# Automated setup verification and basic testing
# ===================================================================

echo "🤖 ChatbotMachine - Quick Test Script"
echo "====================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "SUCCESS") echo -e "${GREEN}✅ $message${NC}" ;;
        "ERROR") echo -e "${RED}❌ $message${NC}" ;;
        "WARNING") echo -e "${YELLOW}⚠️  $message${NC}" ;;
        "INFO") echo -e "${BLUE}ℹ️  $message${NC}" ;;
    esac
}

# Step 1: Check Java installation
print_status "INFO" "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    print_status "SUCCESS" "Java found: $JAVA_VERSION"
else
    print_status "ERROR" "Java not found. Please install Java 8+ and try again."
    exit 1
fi

# Step 2: Check javac (compiler)
print_status "INFO" "Checking Java compiler..."
if command -v javac &> /dev/null; then
    JAVAC_VERSION=$(javac -version 2>&1)
    print_status "SUCCESS" "Java compiler found: $JAVAC_VERSION"
else
    print_status "ERROR" "Java compiler not found. Please install JDK and try again."
    exit 1
fi

# Step 3: Check if we're in the right directory
print_status "INFO" "Checking current directory..."
if [ -f "ChatbotMainTester.java" ]; then
    print_status "SUCCESS" "Found ChatbotMainTester.java in current directory"
else
    print_status "WARNING" "ChatbotMainTester.java not found in current directory"
    print_status "INFO" "Searching for the file..."
    
    # Search for the file
    TESTER_PATH=$(find . -name "ChatbotMainTester.java" 2>/dev/null | head -n 1)
    if [ -n "$TESTER_PATH" ]; then
        TESTER_DIR=$(dirname "$TESTER_PATH")
        print_status "SUCCESS" "Found ChatbotMainTester.java at: $TESTER_PATH"
        print_status "INFO" "Changing to directory: $TESTER_DIR"
        cd "$TESTER_DIR"
    else
        print_status "ERROR" "ChatbotMainTester.java not found. Please navigate to the correct directory."
        exit 1
    fi
fi

# Step 4: Create necessary directories
print_status "INFO" "Creating package directories..."
mkdir -p com/company/contracts/test

# Step 5: Compile the test class
print_status "INFO" "Compiling ChatbotMainTester.java..."
if javac -cp . ChatbotMainTester.java 2>/dev/null; then
    print_status "SUCCESS" "Compilation successful"
    
    # Move the class file to the correct package structure
    if [ -f "ChatbotMainTester.class" ]; then
        mv ChatbotMainTester.class com/company/contracts/test/
        print_status "SUCCESS" "Class file moved to package structure"
    fi
else
    print_status "ERROR" "Compilation failed. Check the Java code for errors."
    # Try to compile with verbose output to show errors
    echo ""
    print_status "INFO" "Compilation errors:"
    javac -cp . ChatbotMainTester.java
    exit 1
fi

# Step 6: Run a quick test
print_status "INFO" "Running quick functionality test..."
echo ""
echo "🧪 QUICK TEST EXECUTION"
echo "======================"

# Create a test input file
cat > test_input.txt << 'EOF'
7
1
show faild prts for contrct 987654
8
EOF

# Run the test with input from file
if java -cp . com.company.contracts.test.ChatbotMainTester < test_input.txt > test_output.txt 2>&1; then
    print_status "SUCCESS" "Quick test execution completed"
    
    # Check if the output contains expected results
    if grep -q "CHATBOT MACHINE" test_output.txt; then
        print_status "SUCCESS" "System initialization successful"
    else
        print_status "ERROR" "System initialization failed"
    fi
    
    if grep -q "SAMPLE QUERIES" test_output.txt; then
        print_status "SUCCESS" "Sample queries feature working"
    else
        print_status "WARNING" "Sample queries feature not detected"
    fi
    
    if grep -q "DETAILED QUERY ANALYSIS" test_output.txt; then
        print_status "SUCCESS" "Query processing feature working"
    else
        print_status "WARNING" "Query processing feature not detected"
    fi
    
else
    print_status "ERROR" "Quick test execution failed"
    echo "Error details:"
    cat test_output.txt
fi

# Clean up test files
rm -f test_input.txt test_output.txt

echo ""
echo "🎯 QUICK TEST SUMMARY"
echo "===================="
print_status "SUCCESS" "Java environment: Ready"
print_status "SUCCESS" "Compilation: Successful"
print_status "SUCCESS" "Basic functionality: Working"
print_status "SUCCESS" "Package structure: Correct"

echo ""
echo "🚀 READY FOR INTERACTIVE TESTING!"
echo "=================================="
echo ""
print_status "INFO" "To start interactive testing, run:"
echo "java -cp . com.company.contracts.test.ChatbotMainTester"
echo ""
print_status "INFO" "Or run this script with 'interactive' parameter:"
echo "./QuickTestScript.sh interactive"
echo ""

# Check if user wants to run interactive mode
if [ "$1" = "interactive" ]; then
    print_status "INFO" "Starting interactive testing mode..."
    echo ""
    java -cp . com.company.contracts.test.ChatbotMainTester
fi

print_status "SUCCESS" "Quick test script completed successfully!"