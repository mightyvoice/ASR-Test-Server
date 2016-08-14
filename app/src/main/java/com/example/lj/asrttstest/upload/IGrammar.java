package com.example.lj.asrttstest.upload;

/**
 * Created by lj on 16/6/9.
 * From nuance sample code
 */
interface IGrammar {

    /**
     * Gets the grammar id.
     *
     * @return the id
     */
    String getId();

    /**
     * Gets the grammar type.
     *
     * @return the type
     */
    String getType();

    /**
     * Gets the grammar content category.
     *
     * @return the content category
     */
    String getContentCategory();

    /**
     * Gets the grammar checksum.
     *
     * @return the checksum
     */
    String getChecksum();

}
