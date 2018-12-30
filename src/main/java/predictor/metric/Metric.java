package predictor.metric;

/**
 * Metric
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Metric {

	/**
	 * Distance between two strings
	 * @param first - first string
	 * @param second - second string
	 * @return - distance between strings
	 */
	public int getDistance(CharSequence first, CharSequence second) {
		return getDistance(first, second, -1);
	}

    /**
     * Distance between two strings
     * @param first - first string
     * @param second - second string
     * @param max - max distance
     * @return - distance between strings. If distance greeter than max then returns some arbitrary value definitely greater than max
	 */
	public abstract int getDistance(CharSequence first, CharSequence second, int max);

    /**
     * Prefix distance - distance between prefix and corresponding prefix of string
     * @param string - string
     * @param prefix - prefix
     * @return - prefix distance
     */
	public int getPrefixDistance(CharSequence string, CharSequence prefix) {
		return getPrefixDistance(string, prefix, -1);
	}

    /**
     * Prefix distance - distance between prefix and corresponding prefix of string
     * @param string - string
     * @param prefix - prefix
     * @param max - max distance
     * @return - prefix distance, if distance greeter than max then returns some arbitrary value definitely greeter than max
     */
	public abstract int getPrefixDistance(CharSequence string, CharSequence prefix, int max);

	/**
	 * В зависимости от значения параметра prefix возвращает или префиксное расстояние, или обычное.
	 * If prefix set to true returns prefix distance, else simple distance
	 * @see #getDistance(CharSequence, CharSequence)
	 * @see #getPrefixDistance(CharSequence, CharSequence)
	 */
	public int getDistance(CharSequence first, CharSequence second, boolean prefix) {
		return prefix ? getPrefixDistance(first, second) : getDistance(first, second);
	}

    /**
     * В зависимости от значения параметра prefix возвращает или префиксное расстояние, или обычное.
     * If prefix set to true returns prefix distance, else simple distance
     * @see #getDistance(CharSequence, CharSequence, int)
     * @see #getPrefixDistance(CharSequence, CharSequence, int)
     */
	public int getDistance(CharSequence first, CharSequence second, int max, boolean prefix) {
		return prefix ? getPrefixDistance(first, second, max) : getDistance(first, second, max);
	}
	
	public abstract int[] getVector(int[] previousRow, char ch, int chIndex, CharSequence prefix, int max);
}

    //public getPrefixDistance(CharSequence string, CharSequence prefix, int max)