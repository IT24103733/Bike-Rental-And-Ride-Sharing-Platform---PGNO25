package com.example.oopprojectbeta.utl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuickSort {

    /**
     * Main method to demonstrate the QuickSort functionality
     */
    public static void main(String[] args) {
        // Test with integers
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);
        numbers.add(2);
        numbers.add(9);
        numbers.add(1);
        numbers.add(7);

        System.out.println("Original list: " + numbers);
        sort(numbers);
        System.out.println("Sorted list: " + numbers);

        // Test with strings
        List<String> names = new ArrayList<>();
        names.add("Mountain");
        names.add("City");
        names.add("Electric");
        names.add("Road");

        System.out.println("\nOriginal names: " + names);
        sort(names);
        System.out.println("Sorted names: " + names);

        // Test with custom comparator (sort by length)
        System.out.println("\nSorting names by length:");
        sort(names, Comparator.comparing(String::length));
        System.out.println("Names sorted by length: " + names);
    }

    /**
     * Generic quicksort implementation that can sort any list of objects
     * using the provided comparator.
     * 
     * @param list The list to be sorted
     * @param comparator The comparator to determine the order
     * @param <T> The type of elements in the list
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return; // Already sorted
        }
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSort(list, low, pivotIndex - 1, comparator);
            quickSort(list, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        // Choose the rightmost element as pivot
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // If current element is smaller than the pivot
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                // Swap list[i] and list[j]
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        // Swap list[i+1] and list[high] (or pivot)
        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }

    /**
     * Convenience method for sorting a list of Comparable objects
     * 
     * @param list The list to be sorted
     * @param <T> The type of elements in the list, must implement Comparable
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        sort(list, Comparator.naturalOrder());
    }
}
