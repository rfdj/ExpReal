# ExpReal

ExpReal ('Expressive Realizer') is a multilingual surface realizer designed for generating text in interactive narratives. It allows authors to write templates that will be transformed into text. Under the hood, it uses [SimpleNLG-NL](https://github.com/rfdj/SimpleNLG-NL) for inflection and picking pronouns. SimpleNLG-NL is based on [SimpleNLG](https://github.com/simplenlg/simplenlg). ExpReal is capable of realization in English, French and Dutch.

(It is still being developed further.)

# Installation
Either clone the project or add it as a Maven dependency through [Jitpack](https://jitpack.io). For the latter option, add the following repository to your `POM` file:

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add ExpReal itself:

```
<dependencies>
    <dependency>
        <groupId>com.github.rfdj</groupId>
        <artifactId>ExpReal</artifactId>
        <version>5511e38362</version>
    </dependency>
</dependencies>
```

> Note: As long as there is no real release, use the first 10 characters of a commit hash to get that 'version'. Or use `master-SNAPSHOT` to always get the latest.

# Usage
To use it, add this project as a dependency and initialize an instance of `ExpressiveActionRealizer`, passing it a filename (situated in the resources) and a language. The file contains your templates as described below. For the language, the options are `ERLanguage.ENGLISH`, `ERLanguage.FRENCH` and `ERLanguage.DUTCH`. Each instance of `ExpressiveActionRealizer` can only have one language. Create multiple instance for multiple languages.

```
ExpressiveActionRealizer(String fileName, ERLanguage language)
```
for example:
```
ExpressiveActionRealizer ear = new ExpressiveActionRealizer("TemplatesFile.csv", ERLanguage.ENGLISH);
```

The most important method of the realizer is `ear.getTexts()`. That method is responsible for the actual realization and returns the resulting text. As parameters, it requires a predicate and a context. See below for a full example.

## Full example
> Make sure you've built a template file according to the specifications in the next section.
```
ExpressiveActionRealizer ear = new ExpressiveActionRealizer("TemplatesFile.csv", ERLanguage.ENGLISH);

/* Construction of a predicate */
Vector<ERArgument> arguments = new Vector<>();
arguments.add(new ERArgument("task", "brush"));
arguments.add(new ERArgument("argument", "teeth"));
ERPredicate myPredicate = new ERPredicate("giveMeSomething", arguments);

/* Construction of a context */
ERPerson julia = new ERPerson("julia", ERGender.FEMININE);
ERPerson frank = new ERPerson("frank", ERGender.MASCULINE);

ERContext context = new ERContext(julia, frank);
context.setSpeaker(julia);
context.setListener(frank);

/* Get results */
Vector<String> result = ear.getTexts(myPredicate, context);

for (String utterance : result) {
    System.out.println(utterance);
}
```
The result should be Julia asking Frank to brush his teeth: 'Please, brush your teeth!'
For more examples, have a look in the src/test/java folder.

## Authoring templates
All templates are stored in a single file to make it easier to translate and coordinate. It's a simple CSV file with five columns (separated by semicolons):

| Predicate name | Conditions | English | French | Dutch|
|---|---|---|---|---|

Example:


| Predicate name | Conditions | English | French | Dutch|
|---|---|---|---|---|
|giveMeSomething| |Please, $task!|S'il te plait $task!| $task, alsjeblieft!|
|brush| |brush your %argument|brosse-toi %argument|poets je %argument|
|teeth| |teeth|les dents|tanden|
|julia| |Julia|Julia|Julia|
|frank| |Frank|François|Frank|

### Template syntax
Each template can consist of plain 'canned' text or include different types of variables to make it more dynamic. There are two types of variable to be used in the text of the template itself:
1. context variables: starting with `%`, such as names of characters or items 
1. predicate variables: starting with `$` for subclauses, tasks etc.

A third type of non-text items in templates are so-called 'grammatical blocks'. These indicate the grammatical role of the word contained within it. That helps with inflection and referring expression generation.

> Note: Grammatical blocks can contain variables, too!

Example:

```
{subject: %julia} {verb: like} eating fries.
``` 

This can result in `Julia likes eating fries.` or `I like eating fries.` or even `You like eating fries.` based on the context.

# License
ExpReal is released under the Apache 2.0 license. See the LICENSE file for details.



