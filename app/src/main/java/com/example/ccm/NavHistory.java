package com.example.ccm;

import android.content.Intent;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavHistory {
    private static final Deque<Intent> forward = new ArrayDeque<>();

    public static void push(Intent intent) { forward.push(intent); }

    public static Intent pop() { return forward.isEmpty() ? null : forward.pop(); }

    public static boolean hasForward() { return !forward.isEmpty(); }

    public static void clear() { forward.clear(); }
}