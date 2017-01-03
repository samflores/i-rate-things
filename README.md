I rate things

# Requirements

- JVM
- Clojure
- [Boot](https://github.com/boot-clj/boot#install)
- [Firebase CLI](https://firebase.google.com/docs/web/setup) (`npm install -g firebase-tools`)

# Runnig locally

- Configure `resources/firebase.edn` with your firebase application token and
  URLs. There's a sample configuration file at `resources/firebase.sample.edn`.

```
$ boot dev
```

And load http://localhost:8080 in the browser

# Build production version and deploy to firebase

```
$ boot production build target && firebase deploy
```

# Load sample data into Firebase

On the [Firebase Console](https://console.firebase.google.com), select your
application, then the Database option on the left navigation bar. You can
manually edit the database "JSON" or import the `resources/sample-ratings.json`
file using the three dots menu on the right corner.
