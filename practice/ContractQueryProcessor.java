private String performSpellCorrection(String originalQuery) {
    try {
        String correctedQuery = spellChecker.performComprehensiveSpellCheck(originalQuery);
        
        // Log corrections if any were made
        if (!originalQuery.equals(correctedQuery)) {
            logger.info(String.format("Comprehensive spell correction: '%s' -> '%s'", 
                                     originalQuery, correctedQuery));
        }
        
        return correctedQuery;
        
    } catch (Exception e) {
        logger.log(Level.WARNING, "Spell correction failed, using original query", e);
        return originalQuery;
    }
}