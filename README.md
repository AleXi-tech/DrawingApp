# Drawing App

A simple and easy-to-use drawing app for Android that allows users to draw on a canvas with different brush sizes and colors, add background images, and save or share their creations.

<table>
  <tr>
    <td>
      <img src="https://user-images.githubusercontent.com/67434047/232354190-e7c50561-663c-4d48-ade3-0cd6350435b7.png" width="500">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/67434047/232354630-93f458ba-e762-4535-9bc3-7d0d68b15ecb.png" width="500">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/67434047/232354695-e864035d-3a37-4aed-a0b4-05db1a471be0.png" width="500">
    </td>
  </tr>
</table>





## Features

- Draw with different brush sizes and colors
- Add a background image from the device's gallery
- Undo the last drawn path
- Save the final image as a PNG file
- Share the saved image with other apps

## Technologies Used

- **Bitmap and Canvas**: The app utilizes the Bitmap and Canvas classes to manipulate images, render the drawn paths on the canvas, and save the final image.
- **Custom Views**: The app makes use of custom views to create a flexible and interactive drawing canvas that can handle touch events and render the drawn paths efficiently.
- **ActivityResultContracts**: The app uses the ActivityResultContracts API to handle runtime permissions and activity results in a more concise and type-safe manner.
- **AlertDialogs and Dialogs**: The app makes use of AlertDialogs and custom Dialogs to provide an interactive user interface for selecting brush sizes, colors, and displaying progress.
- **FileProvider**: The app uses FileProvider to securely share the saved image with other apps, while ensuring the app's internal storage remains protected.
- **Scoped Storage**: The app follows Android's scoped storage best practices, ensuring that it only requests the necessary storage permissions and accesses files in a privacy-friendly manner.

