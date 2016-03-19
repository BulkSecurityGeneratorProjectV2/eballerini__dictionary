package org.dictionary.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.dictionary.api.DashboardAPI;
import org.dictionary.api.LanguageAPI;
import org.dictionary.api.StatAPI;
import org.dictionary.domain.Language;
import org.dictionary.repository.LanguageRepository;
import org.dictionary.repository.search.WordSearchRepository;
import org.dictionary.translator.LanguageTranslator;
import org.dictionary.translator.StatTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultDashboardService implements DashboardService {

    private final Logger log = LoggerFactory.getLogger(DefaultDashboardService.class);

    @Inject
    private WordSearchRepository wordSearchRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private LanguageTranslator languageTranslator;

    @Inject
    private StatTranslator statTranslator;

    @Override
    public DashboardAPI getDashboard() {

        // This is only ok as long as we have very few languages
        List<Language> allLanguages = languageRepository.findAll();

        log.debug("there are {} languages", allLanguages.size());

        DashboardAPI dashboard = new DashboardAPI();
        List<StatAPI> stats = getStats(allLanguages);
        dashboard.setStats(stats);

        return dashboard;
    }

    private List<StatAPI> getStats(List<Language> allLanguages) {
        List<StatAPI> stats = new ArrayList<StatAPI>();
        for (Language language: allLanguages) {
            LanguageAPI languageAPI = languageTranslator.toAPI(language);
            StatAPI stat = statTranslator.toAPI(languageAPI, countWords(languageAPI.getId()));
            stats.add(stat);
        }
        return stats;
    }

    private int countWords(long languageId) {
        int numWordsForLanguage = wordSearchRepository.countByLanguageId(languageId);
        log.debug("num of words for language with id {}: {}", languageId, numWordsForLanguage);
        return numWordsForLanguage;
    }
}
