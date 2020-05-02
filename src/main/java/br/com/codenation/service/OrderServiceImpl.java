package br.com.codenation.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		return items.stream()
				.mapToDouble(orderItem -> productRepository.findById(orderItem.getProductId())
						.map(product -> product.getIsSale() ? product.getValue() * 0.80 : product.getValue())
						.orElse(0.00) * orderItem.getQuantity())
				.sum();
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		return ids.stream()
				.map(aLong -> productRepository.findById(aLong))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderItem>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		return orders.stream()
				.mapToDouble(orderItems -> calculateOrderValue(orderItems))
				.sum();
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		return productIds.stream()
				.map(aLong -> productRepository.findById(aLong))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.groupingBy(Product::getIsSale));
	}

}