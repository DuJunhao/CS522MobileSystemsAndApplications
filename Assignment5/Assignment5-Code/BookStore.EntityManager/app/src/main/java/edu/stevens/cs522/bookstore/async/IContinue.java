package edu.stevens.cs522.bookstore.async;

import static android.R.attr.value;

/**
 * Created by dduggan.
 */

public interface IContinue<T> {
    public T kontinue(T value);
}

