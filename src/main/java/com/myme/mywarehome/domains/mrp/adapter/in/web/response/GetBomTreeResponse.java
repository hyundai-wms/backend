package com.myme.mywarehome.domains.mrp.adapter.in.web.response;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.product.application.domain.Product;

import java.util.*;
import java.util.stream.Collectors;

public record GetBomTreeResponse(
        Map<String, BomNode> bomTree
) {
    public record BomNode(
            String productName,
            Long companyId,
            int eachCount,
            Map<String, BomNode> children
    ) {
        public static BomNode from(Product product, Map<String, BomNode> children) {
            return new BomNode(
                    product.getProductName(),
                    product.getCompany().getCompanyId(),
                    product.getEachCount(),
                    children
            );
        }
    }

    public static GetBomTreeResponse from(List<BomTree> bomTrees) {
        Map<String, BomNode> result = new HashMap<>();
        Map<String, List<BomTree>> parentChildrenMap = bomTrees.stream()
                .collect(Collectors.groupingBy(
                        bomTree -> bomTree.getParentProduct().getProductNumber()
                ));

        // Find root node (engine product) for the given engine type
        Optional<BomTree> rootBomTree = bomTrees.stream()
                .filter(tree -> tree.getParentProduct().getProductNumber().startsWith("10000-"))
                .findFirst();

        if (rootBomTree.isPresent()) {
            Product rootProduct = rootBomTree.get().getParentProduct();
            BomNode rootNode = buildBomTree(rootProduct, parentChildrenMap);
            result.put(rootProduct.getProductNumber(), rootNode);
        }

        return new GetBomTreeResponse(result);
    }

    private static BomNode buildBomTree(
            Product product,
            Map<String, List<BomTree>> parentChildrenMap
    ) {
        String productNumber = product.getProductNumber();
        List<BomTree> children = parentChildrenMap.getOrDefault(productNumber, Collections.emptyList());

        if (children.isEmpty()) {
            return BomNode.from(product, Collections.emptyMap());
        }

        Map<String, BomNode> childNodes = new HashMap<>();
        for (BomTree child : children) {
            Product childProduct = child.getChildProduct();
            childNodes.put(
                    childProduct.getProductNumber(),
                    buildBomTree(childProduct, parentChildrenMap)
            );
        }

        return BomNode.from(product, childNodes);
    }
}
