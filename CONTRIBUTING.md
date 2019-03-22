# Contribution rules

1. During development we are following GitHub flow. In order to provide change or fix in project, one has to:

   1. Create a branch from the repository.
   2. Create, edit, rename, move, or delete files.
   3. Send a pull request from your branch with your proposed changes to kick off a discussion.
   4. Make changes on your branch as needed. Your pull request will update automatically.
   5. Merge the pull request once the branch is ready to be merged.
   6. Tidy up your branches using the delete button in the pull request or on the branches page.

   For more information see [Understaing the GitHub flow](https://guides.github.com/introduction/flow/).

2. Each commit must contain number of the JIRA ticket. Provide clear and descriptive commit messages.

3. Source code must be formatted according to [Java Google Style](./config/intellij/intellij-java-google-style.xml).
   
   - To setup code formatter in Intellij:
     
     1. Go to **Editor** -> **Code Style** -> **Java**
     2. Import [./config/intellij/intellij-java-google-style.xml](./config/intellij/intellij-java-google-style.xml) scheme.

4. Source code must be checked according to [Jamf checks](./config/checkstyle/checkstyle.xml). 
  
   - To setup checkstyle formatter in Intellij (**CheckStyle-IDEA** plugin is required):

     1. Go to **Checkstyle**
     2. Import and activate [./config/checkstyle/checkstyle.xml](./config/checkstyle/checkstyle.xml) checks.
