package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

public interface SearchDAO {

    public String TABLE_IE_NAME = "search_ie_preprocessed";
    public String TABLE_DD_NAME = "search_dd_preprocessed";
    public String TABLE_RSI_NAME = "search_rsi_preprocessed";
    public String ATTR_ID = "ieId";
    public String ATTR_VALUE = "searchable";

    public List<Long> listIeIdsByFulltextSearchOfIe(String[] queryTokens, Integer limit) throws DatabaseException;

    public List<Long> listIeIdsByFulltextSearchOfDd(String[] queryTokens, Integer limit) throws DatabaseException;

    public List<Long> listIeIdsByFulltextSearchOfRsi(String[] queryTokens, Integer limit) throws DatabaseException;

}
