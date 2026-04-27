Project #8 <(2,4) Search Tree>
CS 2210 – Spring 2026
<David Tarwater and Jadon Spears>

Note: You may not use generative AI to complete this coversheet.

# Requirements
Restate the problem specification, and any detailed requirements

> Our goal was to develop a Java package which implements an ADT for a (2,4) search tree. Our ADT is able to store ordered information, and be accessed according to the Dictionary interface which is what we were in a sense implementing. Our ADT can store any Object. However, we only tested it for the Integer type. We used the provided IntegerComparator. If the user provides a key that is not of the expected type (Integer), it throws an InvalidObjectException. We did not use JUnit testing for this project, but our testing includes an automated test that inserts and removes >= 10,000 random integers, and fully empties the tree.

# Design
How did you attack the problem? What choices did you make in your design, and why? Show class diagrams for more complex designs.

> We walked through this project systematically as we walked through the slides and helpful instructions. We started out by creating our 2 helper functions, Find first Greater than and Equal To (FFGTE) and What Child Is This (WCIT). Once we had both of these functions we were able to use the FFGTE for the FindElement function. We quickly finished that up and began on the Insert Function. That one took a while, but I split it up after finding where to insert, and inserting it. When I check for overflow I call a seperate splitNode function that handles overflow and restructures the tree as needed. Once overflow was handled and we were able to insert elements without any errors (which took sometime as we messed up pointer issues at first) we worked on handling duplicates, and then eventually began on the remove function.

# Security Analysis
State the potential security vulnerabilities of your design. How could these vulnerabilities be exploited by an adversary? What would be the impact if the vulnerability is exploited?

> N/A

# Implementation
Outline any interesting implementation details.

> There was no particularly interesting details with regards to the implementaion of this project. However, it was very cool to work with a larger scale project as we were working with multiple interfaces and classes to develop a fairly complex data structure. I enjoyed working through the and problem solving during debugging as I slowly found the problems in my code, at times frustrating, but very satisfying when the problem was found. Loved the way we could test our code as we went along in the project to have assurance in our insert function before begining the remove function and all the debugging that took place during that.

# Testing
Explain how you tested your program, enumerating the tests if possible.
Explain why your test set was sufficient to believe that the software is working properly,
i.e., what were the range of possibilities of errors that you were testing for.

> All testing was within specified given test you instructed and provided for us to use. Seems adequate enough given the large scale number of elements we are implementing and the fact that we are forcing duplicateds and corner cases.

# AI Use
How did you use generative AI in this project?  Be specific!

> We struggled setting up GitHub and used AI as a consult to problem solve some of the errors we were having, however it was not used for any code generation.

# Summary/Conclusion
Present your results. Did it work properly? Are there any limitations? If it is an analysis-type project, this section may be significantly longer than for a simple implementation-type project.

> No limitations within given specification for this project. It is only tested for integers, however it does work properly with integers. It was just an implementation of the given interfaces so it was a fairly straight forward project outside of debugging issues.

# Lessons Learned
List any lessons learned, especially in regards to AI use.

> No lessons learned in regards to AI use on this project.

# Time Spent
Approximately how much time did you spend on this project?

> 20 Hours combined time
