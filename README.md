## Spotlight Compose Library

### Purpose
This application serves as a demonstration of integrating spotlight functionality into a Jetpack Compose app.

### Usage

1. **Add Library Dependencies**

   For Gradle projects:

   - **Step 1**: Add the JitPack repository to your build file. Insert the following code at the end of your root build.gradle within the `repositories` block:

     ```groovy
     dependencyResolutionManagement {
         repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
         repositories {
             mavenCentral()
             maven { url 'https://jitpack.io' }
         }
     }
     ```

   - **Step 2**: Add the dependency:

     ```groovy
     dependencies {
         implementation 'com.github.JyotiBhambhu:spotlight_compose_library:1.0.0'
     }
     ```

   For the latest version, refer to: [JitPack - Spotlight Compose Library](https://jitpack.io/#JyotiBhambhu/spotlight_compose_library)

2. **Usage in Code**

   - Use `Modifier.onGloballyPositioned { coordinates -> }` to obtain the coordinates of the targeted view for which the spotlight is intended.
   
   - Call the `RenderSpotlight()` function from this library to display the spotlight. This function requires the following parameters:

     1. `spotLightIndex`: Index of the current target visible. By default, you can use 0.
     2. `targets`: A list of targets (library class).
     3. `scrollState`: Aids in auto-scrolling targets that are out of view.
     4. `spotlightActions`: Callbacks for actions such as removing a target, moving to the next target, or moving to the previous target.

### Example
```kotlin
// Add this code where spotlight is intended to be shown
Modifier.onGloballyPositioned { coordinates ->
    // Obtain coordinates of the targeted view
}

// Call RenderSpotlight() function to display spotlight
RenderSpotlight(
    spotLightIndex = 0,
    targets = listOf(Target(/* target parameters */)),
    scrollState = rememberLazyListState(),
    spotlightActions = /* provide appropriate callbacks */
)
```

