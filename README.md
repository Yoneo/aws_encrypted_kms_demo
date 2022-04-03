# AWS S3 Demo symmetric, local encryption with KMS
This repository shows how the Amazon S3 cloud storage can be accessed using Java. It up- and downloads an sample file via client side symmetric encryption to AWS S3. Maven is used for dependency management.

Tested with Java version: `JavaSE-17`

The objective is:  
-Download of a CSV file  
-Upload of a CSV file  
-Symmetric encryption is to be used by default.  
-The login data is stored as variables.  

The Bouncy Castle JAR has to be included in build path. Download at [bouncycastle.org](https://bouncycastle.org/latest_releases.html)   
You can check the necessary AWS IAM policy settings in the file `IAM_policy.txt`.  

License: CC0

For detailed documentation see file `Blueprint AWS.md`.
