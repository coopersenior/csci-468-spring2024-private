# Catscript Guide

Documentation by Cooper Senior

## Introduction

Catscript is a simple scripting language.  Here is an example:

```
var x = "foo"
print(x)
```

CatScript is statically typed, with a small type system as follows:

```
int - a 32 bit integer
string - a java-style string
bool - a boolean value
list - a list of value with the type 'x'
null - the null type
object - any type of value
```

## Features

### For loops

Catscript supports for loops to iterate over elements in a list, 
enhancing the language's functionality. This feature allows 
programmers to efficiently process each item in the list, 
executing specific actions or operations as desired. With for 
loops, Catscript allows developers to implement the 
logic for handling data structures effectively.

Example:
```
for(x in [1, 2, 3]) { print(x) }

1  2  3
```

### If Statements
Catscript supports if statements to offer flexibility in handling various 
scenarios, enabling developers to tailor their code's 
behavior based on specific conditions. The optional else case expands 
this functionality, enabling control flow by accommodating both expected 
and alternative outcomes within the program's logic. 
With if statements, Catscript allows for managing code behavior based 
on dynamic conditions, enhancing its flexibility and utility.

Example:
```
if(true){ print(1) }

1
```
Example with else case:
```
if(false){ print(1) } else { print(2) }

2
```
### Print Statement

The print statement in Catscript allows for 
displaying output to the user or can be used for debugging purposes. 
It enables displaying results, variables, or messages directly 
in the console or terminal window. With its simplicity and 
effectiveness, the print statement delivers clear communication 
between the program and the user. The print statement accepts an expression.

Example:
```
print(1)

1
```

### Variable Statement
The variable statement in Catscript enables for data storage 
and manipulation. It allows for the assignment 
of values to identifiers, creating named storage locations for 
information. Variables are used to manage and update 
data dynamically, enhancing the flexibility and functionality of 
a program.

Example:
```
var x = 1
print(x)

1
```
### Function Declaration

Function declaration statements in Catscript allows for 
defining reusable blocks of code to perform specific tasks. 
They consist of a function name, parameters (if any), the data type
(if any) and the code block containing the actions to be executed. 
By declaring functions, the code can be modularized, improving 
readability, maintainability, and efficiency.

Example:
```
function foo(x) { print(x) }
foo(1)
foo(2)
foo(3)

1 2 3
```
### Recursion

Recursion in Catscript enables functions to call themselves within 
their own definition, allowing for elegant and efficient solutions
to certain problems. This technique involves breaking down a 
problem into smaller, similar subproblems, reducing complexity 
and promoting code reuse. However, it requires careful consideration 
of termination conditions to prevent infinite loops and ensure the 
correct functioning of the recursive algorithm.

Example:
```
function foo(x : int) { 
    print(x)
    if(x > 0) {
        foo(x - 1)
    }
}
foo(9)

9 8 7 6 5 4 3 2 1 0
```

### Return Statement
The return statement in Catscript allows functions to send back a 
value to the code that called them. It marks the end of the 
function's execution and provides a mechanism for passing data 
back to the calling code. Return statements are essential 
for obtaining results from functions and for controlling program 
flow based on the functions outcomes.

Example:
```
function foo(x : int) : int {
    return x + 1
}
print(foo(9))

10
```
### List Literal Expression
List literal expressions in Catscript allow for defining 
lists directly within the code using square brackets and comma-separated 
values. They provide a concise and readable way to initialize lists with 
predefined elements. By using list literals, developers can quickly 
create and manipulate lists without the need for extensive code. Lists can
be multidimensional enhancing their usability.

Example:
```
[1, 2, 3]
```
### Unary Expression
Unary expressions in Catscript involve operations on a single operand, 
such as negation or incrementation. It provides a concise and 
straightforward way to perform unary operations within the code. 
By utilizing unary expressions, developers can efficiently manipulate 
individual values or variables, enhancing the flexibility and 
expressiveness of their programs.

Examples:
```
-1

not true
```
### Factor Expression

Factor expressions in Catscript allows for mathematical operations 
within the code, encompassing variables, constants, or more complex
expressions. Factor expressions serve as the fundamental units 
for computations, forming the basis for arithmetic calculations 
and other mathematical manipulations. By incorporating factor expressions, 
intricate mathematical logic and algorithms can be calculated with 
ease and precision. It's worth noting that you can parenthesis expressions
throughout the language.

Examples:
```
2 * 4 / 2

2 * (3 * 4)
```

### Additive Expression
Additive expressions in Catscript combine two values using addition 
or concatenation operators, depending on the data types involved. 
For numeric values, the addition operator performs arithmetic addition, 
while for strings, it concatenates them together. This allows developers 
to efficiently join strings or Integers to form longer strings, 
facilitating text manipulation and formatting within the code.

Example of arithmetic:
```
1 + 2 - 1

2
```
Example with concatenation:
```
1 + "a"

1a
```

### Comparison Expression
Comparison expressions in Catscript evaluate the relationship between 
two values using comparison operators such as "<", ">", "<=", and ">=". 
These expressions return a boolean value, indicating whether the 
comparison is true or false. This enables developers to make decisions 
and control the flow of their code based on the result of comparisons 
between variables, literals, or other expressions.

Example:
```
1 <= 2
```

### Equality Expression
Equality expressions in Catscript, denoted by "!=" (not equal) or "==" 
(equal), compare two values to determine if they are the same or different. 
When using "==", the expression evaluates to true if the operands are 
equal and false otherwise. Alternatively, "!=" evaluates to true if the 
operands are not equal and false if they are. These expressions are 
fundamental for implementing conditional logic and decision-making
within the codebase.

Example:
```
1 == 1
```

Example 2:
```
true != null
```
