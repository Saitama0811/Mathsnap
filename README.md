# Mathsnap
This app is made from tesseract library which takes photo of mathematical equation and then calculate the result and then show the result on image dialog with image and result.

How this application works?

This app takes a captured image as input and converts that image into a bitmap image.

Crops the image according to the Focus Box.

Then sends this image to the Tesseract OCR library which compiles the image on the Tess Engine trained data.

Now we recieve a string data with recognized text in it.

This function is then evaluated and given as output.

Note#: THis app is still under development and contains many errors.
The trained data is also not trained for the LSTM data(handwritten data). It only works upon the normal digital big font.
