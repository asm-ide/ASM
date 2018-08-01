# Contributing Guideline
### 1. Use annotations.
You can use `@NonNull` or `@Nullable` at the every code.<br>

### 2. Make javadocs in all files.
Other contributors and people can understand code easily, if javadoc exists.<br>
**Making javadoc might be annoying, but it will help your code to improve.**

### 3. Write our short version of license, which is revealed at the end of [LICENSE](LICENSE).
**Notification: currently license is not developed and not have to do this.**<br>

### 4. Unify the code style.
Our code style is defined as:<br>

#### global:
Indent style: Tab(width : 4 space<br>
Space between brackets: none `(...)`<br>
Space between operators: 1 `abc = 123;`<br>

#### java, c, c++:
U = upper case, l = lower case<br>
class name: UllUlll... (upper case at first)<br>
function name: llUllll... (lower case at first)<br>
constant name: UUU_UUUU... (all upper)

function: 
```
... myfunc() {
    ...
}
```
class, namespace(c++): 
```
... MyClass
{
    ...
}
```
for: `for(int i = 0; i < max; i++) { ... }`

#### xml
attributes: 
```
<tag value="abc"
    value2="123">
```
or 
```
<a_very_long_taggy_tag
   value1="123">
```
space before `/>`: 1 `<mytag hello="hi" />`

<p>

> **We :heart: our all contributors.**
