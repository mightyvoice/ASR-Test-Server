package com.example.lj.asrttstest.upload;

/**
 * Created by lj on 16/6/9.
 */
/**
 * A Grammar represents an NCS Per User Grammar data object.
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class Grammar implements IGrammar {

    /** The grammar id. */
    private String mId = null;

    /** The grammar type. */
    private String mType = null;

    /** The grammar content_category. */
    private String mContentCategory = null;

    /** The grammar checksum. */
    private String mChecksum = null;

    /**
     * Instantiates a new grammar.
     *
     * @param id the grammar id
     * @param type the grammar type
     * @param content_category the grammar content_category
     * @param checksum the grammar checksum
     */
    public Grammar(String id, String type, String content_category, String checksum) {
        mId = id;
        mType = type;
        mContentCategory = content_category;
        mChecksum = checksum;
    }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.IGrammar#getId()
	 */
    @Override
    public String getId() { return mId; }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.IGrammar#getType()
	 */
    @Override
    public String getType() { return mType; }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.IGrammar#getContentCategory()
	 */
    @Override
    public String getContentCategory() { return mContentCategory; }

    /* (non-Javadoc)
	 * @see com.nuance.dragon.toolkit.sample.IGrammar#getChecksum()
	 */
    @Override
    public String getChecksum() { return mChecksum; }
}

