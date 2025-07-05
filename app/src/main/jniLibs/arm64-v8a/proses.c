#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <android/log.h>

#define TAG "proses"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_go_all_Terminal_JalankanPerintah(JNIEnv *env, jobject obj, jstring jperintah) {
    const char *perintah = (*env)->GetStringUTFChars(env, jperintah, 0);
    
    const char *rootfs = "/data/data/go.all/files/logika/rootfs";
    const char *proot = "./proot";
    
    if (access(proot, X_OK) != 0) {
        (*env)->ReleaseStringUTFChars(env, jperintah, perintah);
        return (*env)->NewStringUTF(env, "PRoot tidak ada/tidak executable");
    }
    
    if (access(rootfs, F_OK) != 0) {
        (*env)->ReleaseStringUTFChars(env, jperintah, perintah);
        return (*env)->NewStringUTF(env, "RootFS tidak ditemukan");
    }

    char cmd[2048];
    snprintf(cmd, sizeof(cmd),
             "%s -S %s -w /root -b /dev -b /proc -b /sys /bin/sh -c \"%s\" 2>&1",
             proot, rootfs, perintah);

    LOGD("Executing: %s", cmd);

    FILE *fp = popen(cmd, "r");
    if (!fp) {
        (*env)->ReleaseStringUTFChars(env, jperintah, perintah);
        return (*env)->NewStringUTF(env, "Gagal menjalankan perintah");
    }

    char buffer[256];
    char output[8192] = {0};

    while (fgets(buffer, sizeof(buffer), fp)) {
        strncat(output, buffer, sizeof(output) - strlen(output) - 1);
    }

    int status = pclose(fp);
    if (status != 0) {
        LOGD("Command exited with status %d", status);
    }

    (*env)->ReleaseStringUTFChars(env, jperintah, perintah);
    return (*env)->NewStringUTF(env, output);
}