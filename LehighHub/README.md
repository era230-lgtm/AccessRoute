# Lehigh Hub

A small, local-only hackathon prototype: a campus home page, club bulletin board, dining-dollar budget helper, transit route board, student resource directory, and editable demo profile. Club and resource listings are examples; the bus board shows the supplied route-stop database and base frequencies, without live arrivals. The app is not connected to Lehigh University systems.

## Requirements

- A JDK installed on your Mac. Visual Studio Code does not include Java by itself. Install a JDK before running the app if `java` and `javac` are not available in the integrated terminal.
- No external libraries or package manager are required. `com.sun.net.httpserver.HttpServer` and the other Java imports used here are provided by the JDK.

## Run from the VS Code integrated terminal

1. Open the `LehighHub` folder in VS Code.
2. In the integrated terminal, move to that folder if needed:

   ```sh
   cd /path/to/LehighHub
   ```

3. Homebrew installs this JDK as a separate version. Make it available in this terminal:

   ```sh
   export PATH="/usr/local/opt/openjdk@21/bin:$PATH"
   ```

   To make this available in future zsh terminals too, add that `export` line to `~/.zshrc`.

4. Compile the Java source files:

   ```sh
   javac -d out src/com/lehighhub/*.java
   ```

5. Start the server:

   ```sh
   java -cp out com.lehighhub.LehighHubApplication
   ```

6. Open [http://localhost:8080](http://localhost:8080).

Keep the terminal open while presenting. Press **Control+C** to stop the server. Start it from the `LehighHub` folder so the server can find `public/css`.

## Page map

- `/` — welcome page and links to each section
- `/clubs` — sample clubs and a local post preview form
- `/dining` — daily dining budget calculator
- `/buses` — local-time route availability, ordered stops, and a coordinate-based map from the supplied transit data
- `/resources` — official Lehigh academic, campus support, billing, career, and news-access links
- `/signup` — creates an in-memory demo profile with clickable interests
- `/profile` — edits profile details and uploads a local profile picture

All interactions use ordinary HTML forms and Java request handling. Form submissions reload the page; no JavaScript is used. Demo profiles are kept in server memory and reset when the server stops. The signup password field is only a visual prototype: the password is discarded and never used for authentication. Uploaded profile pictures are saved under `data/profile-pictures` on the computer running the server.
