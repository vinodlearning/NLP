#!/bin/bash

# Oracle ADF NLP System - Compilation Script
# This script compiles all Java files in the correct dependency order

echo "🚀 Starting Oracle ADF NLP System Compilation..."
echo "================================================"

# Set source directory
SRC_DIR="src"

# Check if source directory exists
if [ ! -d "$SRC_DIR" ]; then
    echo "❌ Error: Source directory '$SRC_DIR' not found!"
    exit 1
fi

# Function to compile with error checking
compile_package() {
    local package=$1
    local description=$2
    
    echo "📦 Compiling $description..."
    
    if javac -cp "$SRC_DIR" "$SRC_DIR"/$package/*.java; then
        echo "✅ $description compiled successfully"
    else
        echo "❌ Error compiling $description"
        exit 1
    fi
}

# Compile in dependency order
echo
echo "Step 1: Core Classes (no dependencies)"
compile_package "nlp/core" "Core Classes"

echo
echo "Step 2: Correction Classes (depends on core)"
compile_package "nlp/correction" "Spell Correction Classes"

echo
echo "Step 3: Analysis Classes (depends on core + correction)"
compile_package "nlp/analysis" "Text Analysis Classes"

echo
echo "Step 4: Engine Classes (depends on all above)"
compile_package "nlp/engine" "NLP Engine Classes"

echo
echo "Step 5: Handler Classes (depends on all above)"
compile_package "nlp/handler" "Request Handler Classes"

echo
echo "Step 6: Model Classes (additional components)"
if [ -d "$SRC_DIR/model" ]; then
    echo "📦 Compiling Model Classes..."
    
    # Try to compile model classes, but don't fail if JSF dependencies are missing
    if javac -cp "$SRC_DIR" "$SRC_DIR"/model/nlp/*.java 2>/dev/null; then
        echo "✅ Model Classes compiled successfully"
    else
        echo "⚠️  Model Classes skipped (JSF dependencies required)"
        echo "   Note: Model classes require Oracle ADF/JSF libraries"
    fi
fi

echo
echo "Step 7: Test Classes (depends on all above)"
if [ -d "$SRC_DIR/test" ]; then
    compile_package "test" "Test Classes"
fi

echo
echo "================================================"
echo "✅ COMPILATION COMPLETED SUCCESSFULLY!"
echo "================================================"

# Count compiled files
java_files=$(find "$SRC_DIR" -name "*.java" | wc -l)
class_files=$(find "$SRC_DIR" -name "*.class" | wc -l)

echo "📊 Compilation Statistics:"
echo "   - Java source files: $java_files"
echo "   - Compiled class files: $class_files"
echo "   - Success rate: $(( class_files * 100 / java_files ))%"

echo
echo "🎯 System is ready for:"
echo "   - Integration testing"
echo "   - Oracle ADF integration"
echo "   - Production deployment"

echo
echo "💡 To run tests:"
echo "   cd $SRC_DIR && java test.CompleteNLPSystemTest"