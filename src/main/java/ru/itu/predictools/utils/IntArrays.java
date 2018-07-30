package ru.itu.predictools.utils;

import java.util.function.BiPredicate;

/**
 * in addition to standard Arrays functionality
 * adds primitive types sorting capabilities
 * adds min() function (minimal array member value) etc.
 */
public class IntArrays {
    private static final int INSERTIONSORT_THRESHOLD = 7;

    public static int min(int[] array, BiPredicate<Integer, Integer> comparator){
            int min = array[0];
            for (int i : array) {
                if (comparator.test(min, i)) min = i;
            }
            return min;
    }

	public static void sort(int[] array, BiPredicate<Integer, Integer> comparator) {
		int[] source = array.clone();

		mergeSort(source, array, 0, array.length, 0, comparator);
	}

	private static void mergeSort(int[] src, int[] dest, int low, int high, int off, BiPredicate<Integer, Integer> comparator) {
		int length = high - low;

		if (length < INSERTIONSORT_THRESHOLD) {
			for (int i = low; i < high; i++)
				for (int j = i; j > low && comparator.test(dest[j - 1], dest[j]); j--)
					swap(dest, j, j - 1);
			return;
		}

		int destLow = low;
		int destHigh = high;
		low += off;
		high += off;
		int mid = (low + high) >> 1;
		mergeSort(dest, src, low, mid, -off, comparator);
		mergeSort(dest, src, mid, high, -off, comparator);

		if (comparator.test(src[mid],src[mid - 1])) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}

		for (int i = destLow, p = low, q = mid; i < destHigh; i++)
			if (q >= high || p < mid && comparator.test(src[q],src[p]))
				dest[i] = src[p++];
			else dest[i] = src[q++];
	}

	private static void swap(int[] x, int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
}
