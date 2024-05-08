# Catscript Documentation
by William Jordan

## Introduction

Catscript is a basic coding language designed for educational uses.

## Typeing
Catscript is a statically typed language, there are 6 types:

```
bool — A boolean value (true or false)
int — 32 bit signed integer 
list — Ordered set of values of one or many types
null — Null type
object — Parent type that can contain complex structures
string — A sequence of characters
```

## Expressions

### additive
Additive expressions in Catscript ether preform additive arithmetic on two integers ore are used for string 
concatenation. When dealing with only integers, addition or subtraction can be used. If ether the left or right hand side
of an additive is a string then the values will be concatenated togther. 

```
//addition
1+1         // 2

//subtraction
2-1         // 1

//mixed type concatenation
"a" + 1     // a1

//string concatenation
"a" + "b"   // ab
```

### factor
Factor expressions in Catscript ether preform multiplication or division on two integers. 
```
//multiplication
4 * 2   // 8

// division
4 / 2   // 2
```

### comparison
Comparison expressions in Catscript evaluate inequalities between integers. Using < (less than), > (greater than), <= 
(less than or equal to), >= (greater than or equal to) a boolean value is returned. This expression allows for expanded 
control flow options when used in an if statement.
```
// greater than
2 > 3       // true

// less than or equal with a var
var x = 3
x <= 1      // false
```

### list literal
List literal expressions in Catscript are how lists are defined. A list can contain all of one type, sub lists or mixed 
types. Lists allow for programmers more efficient and higher level ways or storing and organizing data. 
```
//all ints
x = [2, 2, 3]

//list of lists
x = [[1, 2] [3,4]]

//mixed list
x = [1, true, "a"]
```

### equality
Equality expressions in Catscript checks where to values are equal or not equal to each other. Using the == for equality 
and != for not equal to programmers can check the values of variables or see if two variables are equal.
```
// equality check
1 == 1      // true

//variable equality check
var x = 1
x  != 2     // false

//variable compared to variable
x == x      // true
```

### parentheses
In Catscript parentheses within an expression allows for overriding traditional order of operations and have part of 
an expression evaluate first in Catscript. This allows for programmers to implement much more complicated math 
expressions and adjust the values of variables more precisely. 
```
//parenthesized expression
4 * (2 + 2)     // 16
```

### unary
Unary expressions in Catscript evaluate to the negative value of an integer or the opposite value of a boolean. This 
allows for programmers to efficiently manipulate variables within their code. 
```
//negative integer
-1

//negative variable
-x

//flip boolean
not true
```


## Statements

### print
In CatScript, print statements are used to display information to the user. It's a fundamental tool for communicating 
information from the program to the user, facilitating interaction and conveying results or messages during program 
execution. Programmers can pass variables, strings, or expressions to a print statement.

```
//variable
print(x)

//expression
print(1+2)

//string
print("hello world!")
```

### if
In CatScript, if statements are fundamental for controlling program flow. Each if statement includes a boolean 
expression, which determines whether the subsequent code block executes. If the expression evaluates to true, the body 
of the if statement is executed. Optionally, an else statement can be included to handle cases where the boolean 
expression evaluates to false. This allows for conditional execution of different code blocks based on specific 
conditions within the program.

```
//basic if statement
if(true) {
    print(1);
}

// if statement with else
if(1 == 2) {
    print(1)
} else {
    print(2)
}
```
### for
For statements in CatScript iterate over a sequence of elements, such as lists or ranges. They execute a block of code 
repeatedly, each time with a different element from the sequence. This facilitates tasks like processing each item in 
a list or executing a block of code a predetermined number of times.
```
for(x in [1,2,3]) {
    print(x)
}
```
### function
In CatScript, function statements are essential for organizing and encapsulating code. Each function typically includes 
a block of code that performs a specific task or calculation. When invoked, the function executes its code block and may
optionally return a value using a return statement. Parameters can be specified to functions to customize their behavior 
as well as return types.
```
//basic function
function foo() {
    print("hi")
}

//function with a paramater
function foo(x: int) {
    print(x)
}

//function with paramaters and type
function foo(x: int, y: int) : int {
    return x + y
}
```


### return
In CatScript, return statements are fundamental for controlling program flow. Each return statement typically includes a
value or expression to be returned from a function or method. When executed, the return statement immediately exits the 
function or method and passes the specified value back to the caller. Optionally, multiple return statements can be used
within a function or method to handle different conditions or scenarios. This allows for conditional return of values 
based on specific conditions within the program.

```
//variable
function foo(x: int) : int {
    return x
}

//string
function foo(x: int) : int {
    return "success"
}

// return just to exit
function foo(x: int) : int {
    return 
}
```

### var

In CatScript, var statements define and initialize variables. They consist of declaring a variable name and optionally a
type and or an initial value. These statements allow for dynamic data management, memory allocation, and state 
maintenance within the program.

```
//var no type specified
var x = 1

//var with type
var x : int = 10
```

