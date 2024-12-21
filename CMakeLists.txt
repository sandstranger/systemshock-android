cmake_minimum_required(VERSION 3.4.1)

set (ENABLE_SDL2 ON)
set (ENABLE_SOUND ON)
set (ENABLE_FLUIDSYNTH ON)

set(OPENGL_LOCATION ${CMAKE_SOURCE_DIR}/gl4es)
set(OPENGL_INCLUDE_DIR ${OPENGL_LOCATION}/src/main/jni/gl4es/include)
set(OPENGL_INCLUDE_DIRS ${OPENGL_LOCATION}/src/main/jni/gl4es/include)
set(OPENGL_opengl_LIBRARY ${OPENGL_LOCATION}/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libGL.so)
set(OPENGL_glx_LIBRARY ${OPENGL_LOCATION}/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libGL.so)
set(OPENGL_LIBRARIES ${OPENGL_LOCATION}/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libGL.so)

set(SDL2_DIR ${CMAKE_SOURCE_DIR}/sdl2)
set(SDL2_LIBRARY ${CMAKE_SOURCE_DIR}/sdl2/android-project/app/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libSDL2.so)
set(SDL2MAIN_LIBRARY ${CMAKE_SOURCE_DIR}/sdl2/android-project/app/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libSDL2.so)

set (FLUIDSYNTH_DIR ${CMAKE_SOURCE_DIR}/fluidsynth/src/main)
set(FLUIDSYNTH_LIBRARY ${CMAKE_SOURCE_DIR}/fluidsynth/src/main/jniLibs/${ANDROID_ABI}/libfluidsynth.so)

set(SDL2_MIXER_DIR ${CMAKE_SOURCE_DIR}/sdl2-mixer/src/main/jni/SDL_mixer)
set(SDL2_MIXER_LIBRARY ${CMAKE_SOURCE_DIR}/sdl2-mixer/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libSDL2_mixer.so)

add_subdirectory("${CMAKE_SOURCE_DIR}/app/src/main/jni/systemshock")