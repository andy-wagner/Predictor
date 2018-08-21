package ru.itu.predictor.metric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Метрика Левенштейна.
 */
public class LevensteinMetric extends Metric {
  private static final Logger LOGGER = LogManager.getLogger();
  private int[] currentVector;
  private int[] previousVector;
  
  //	public LevensteinMetric() { this(Integer.MAX_VALUE - 2); }
  public LevensteinMetric(int maxStringLength) {
    previousVector = new int[maxStringLength + 2]; //first cell for distance from zero length char-sequence, and last value for minimum from all values of vector
    currentVector = new int[maxStringLength + 2];
    LOGGER.debug("Levenstein Metric has created.");
  }
  
  
  /**
   * {@inheritDoc} Computational complexity O((max + 1) * min(first.length() * second.length()))
   */
  @Override
  public int getDistance(CharSequence first, CharSequence second, int max) {
    int firstLength = first.length();
    int secondLength = second.length();
    
    if (firstLength == 0)
      return secondLength;
    else if (secondLength == 0)
      return firstLength;
    
    if (firstLength > secondLength) {
      CharSequence tmp = first;
      first = second;
      second = tmp;
      firstLength = secondLength;
      secondLength = tmp.length();
    }
    
    if (max < 0 || max > secondLength) max = secondLength;
    if (secondLength - firstLength > max) return max + 1;
    
    currentVector = new int[firstLength + 2];
    previousVector = new int[firstLength + 2];
    
    for (int i = 0; i <= firstLength; i++)
      previousVector[i] = i;
    
    for (int row = 1; row <= secondLength; row++) {//vertical cycle (by rows) Cycle by symbols of a longer string
      char ch = second.charAt(row - 1);
      currentVector[firstLength + 1] = currentVector[0] = row;
      
      currentVector = getVector(previousVector, ch, row, first, max);
      
      int tempRow[] = previousVector;
      previousVector = currentVector;
      currentVector = tempRow;
    }
    return previousVector[firstLength];
  }
  
  /**
   * {@inheritDoc} Levenstein prefix distance - asymptotic calculation time = O((max + 1) * min(prefix.length(), string.length()))
   */
  @Override
  public int getPrefixDistance(CharSequence string, CharSequence prefix, int max) {
    int prefixLength = prefix.length();
    if (max < 0) max = prefixLength;
    int stringLength = Math.min(string.length(), prefix.length() + max);
    
    if (prefixLength == 0)
      return 0;
    else if (stringLength == 0) return prefixLength;
    
    if (stringLength < prefixLength - max) return max + 1;
    
    currentVector = new int[prefixLength + 2];
    previousVector = new int[prefixLength + 2];
    
    for (int i = 0; i <= prefixLength; i++)
      previousVector[i] = i;
    
    int distance = Integer.MAX_VALUE;
    
    for (int row = 1; row <= stringLength; row++) {//vertical cycle (by rows)
      char ch = string.charAt(row - 1);
      currentVector[prefixLength + 1] = currentVector[0] = row;
      
      currentVector = getVector(previousVector, ch, row, prefix, max);
      
      // Calc minimal distance from prefix to all prefixes of target string if their prefix distance <= max
      if (row >= prefixLength - max && row <= prefixLength + max && currentVector[prefixLength] < distance)
        distance = currentVector[prefixLength]; //no need if currentRow[prefixLength]==distance
      
      int tempRow[] = previousVector;
      previousVector = currentVector;
      currentVector = tempRow;
    }
    return distance;
  }
  
  /**
   * Calculate row of Levenstein distance matrix for given string, next char and its position in second string using previously calculated matrix row
   *
   * @param previousRow - earlier calculated matrix row
   * @param ch          - char of second string
   * @param chIndex     - position of char in second string
   * @param prefix      - first string (search string)
   * @param max         - max allowed distance between strings
   * @return - next row of Levenstein distance matrix
   */
  public int[] getVector(int[] previousRow, char ch, int chIndex, CharSequence prefix, int max) {
    int[] currentRow = new int[previousRow.length];
    currentRow[0] = currentRow[previousRow.length - 1] = chIndex + 1; //previousRow[0] + 1;
    // We'll calc only diagonal lane with 2*(max + 1) width
//        int from = Math.max(chIndex - max - 1, 1);
//        int to = Math.min(chIndex + max + 1, previousRow.length-2);//prefixLength=row.length-1

//        for (int column = from; column <= to; column++) {//horizontal cycle (by columns)
    for (int column = 1; column <= previousRow.length - 2; column++) {//horizontal cycle (by columns)
      // Calc current Ld as minimal transfer cost from previous to current state with delete, insert or replace operations
      int cost = prefix.charAt(column - 1) == ch ? 0 : 1;
      currentRow[column] = Math.min(Math.min(currentRow[column - 1] + 1, previousRow[column] + 1), previousRow[column - 1] + cost);
      if (currentRow[column] < currentRow[previousRow.length - 1]) {
        currentRow[previousRow.length - 1] = currentRow[column]; //minimal distance per row
      }
    }
    
    return currentRow;
  }
}
