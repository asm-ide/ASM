package com.asm.block;

import java.util.ArrayList;
import java.util.List;

public class BlockElementsFactory{

    public List<BlockElement> elements;

    public BlockElementsFactory(){
        elements = new ArrayList();
    }


    public BlockElements build(Theme theme){
        return new BlockElements(theme) {

            private List<BlockElement> elements = BlockElementsFactory.this.elements;

            @Override
            public BlockElement get(int index) {
                BlockElement e = elements.get(index);
                e.superElements = this;
                return e;
            }

            @Override
            public int count() {
                return elements.size();
            }
        };
    }
}
