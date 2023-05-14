package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Override
	public CartDto getCartByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		Optional<CartEntity> cartOpt = cartRepository.findById(cartId);
		CartDto returnValue = new CartDto();
		if (cartOpt.isPresent()) {
			returnValue = tempConverter.cartEntityToDto(cartOpt.get());
		} else {
			Error error = new Error("Invalid cart");
			throw new InvalidCartException(error);
		}
		return returnValue;
	}

	@Override
	public void refreshCartState(Integer cartId) {
		// TODO Auto-generated method stub
		CartEntity cartEntity = cartRepository.findById(cartId).get();
		Optional<Float> priceOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId));
		if (priceOpt.isPresent()) {
			cartEntity.setCartPrice(priceOpt.get());
		} else {
			cartEntity.setCartPrice(0f);
		}
		cartRepository.saveAndFlush(cartEntity);
	}

	@Override
	public Float calculateGrandTotal(Integer cartId) {
		// TODO Auto-generated method stub
		Optional<Float> grandTotalOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId));
		Float returnValue = 0f;

		if (grandTotalOpt.isPresent()) {
			returnValue = grandTotalOpt.get();
		}

		return returnValue;
	}

	@Override
	public CartDto validateCart(Integer cartId) {
		// TODO Auto-generated method stub
		Optional<CartEntity> cartOpt = cartRepository.findById(cartId);
		CartDto returnValue = null;
		Error error = new Error("Invalid Cart");

		if (cartOpt.isPresent()) {
			if (cartOpt.get().getCartItems().size() == 0) {
				throw new InvalidCartException(error);
			}

			returnValue = tempConverter.cartEntityToDto(cartOpt.get());

		} else {
			throw new InvalidCartException(error);
		}

		return returnValue;
	}

	@Override
	public List<CartDto> listAll() {
		// TODO Auto-generated method stub
		List<CartDto> returnValue = new ArrayList<CartDto>();
		Optional<List<CartEntity>> allCartsOpt = Optional.ofNullable(cartRepository.findAll());
		if (!allCartsOpt.isEmpty()) {
			allCartsOpt.get().forEach((cart) -> {
				CartDto cartDto = tempConverter.cartEntityToDto(cart);
				returnValue.add(cartDto);
			});
		}
		return returnValue;
	}

	@Override
	public void deleteCart(Integer cartId) {
		// TODO Auto-generated method stub
		cartRepository.deleteById(cartId);
		cartRepository.flush();
	}

}
