# bookScan
Current Status:

  - bookScan will take individually entered user image and convert to black and white image
  
  - black and white image is stored and read using ZXing barcode reader
  
  - barcode is fetched and entered in a website search link
  
  - web scraper finds title of book
  
  - web scraper finds the author(s) of the book


# dependencies 

gradle.build contains the following for the ZXing scanner:

- compile "com.google.zxing:core:3.3.0"

- compile 'com.google.zxing:javase:3.3.0'

# jar files

be sure to add "jaunt1.3.9.jar" to your module dependencies for the web scraper integration
