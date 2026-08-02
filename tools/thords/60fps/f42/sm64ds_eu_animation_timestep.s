	.syntax unified
	.arm

	.global f42_animation_entry
	.type f42_animation_entry, %function
f42_animation_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_advance
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	bxeq	lr

.L_advance:
	.word	0xE92D4010
	.word	0xEA02E8AB

	.align	2
.L_cadence:
	.word	0x0208EE44
.L_game_loop_counter:
	.word	0x020A0DB0
	.size f42_animation_entry, .-f42_animation_entry
