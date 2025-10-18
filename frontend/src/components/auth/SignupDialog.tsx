import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuth } from '@/context/AuthContext';
import { SignupData } from '@/types/user.types';
import { validatePassword, getPasswordErrorMessage, isPasswordValid } from '@/utils/validation';
import { Button } from '@/components/common/Button';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import './AuthDialog.css';

interface SignupDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSwitchToLogin: () => void;
}

export function SignupDialog({ isOpen, onClose, onSwitchToLogin }: SignupDialogProps) {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { register, handleSubmit, watch, formState: { errors }, reset } = useForm<SignupData>();

  const password = watch('password', '');
  const passwordValidation = validatePassword(password);

  const onSubmit = async (data: SignupData) => {
    setError('');
    setIsLoading(true);

    try {
      await signup(data);
      reset();
      onClose();
      navigate('/games');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Signup failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    reset();
    setError('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="dialog-overlay" onClick={handleClose}>
      <div className="dialog-content" onClick={e => e.stopPropagation()}>
        <div className="dialog-header">
          <h2>Sign Up</h2>
          <button className="dialog-close" onClick={handleClose} aria-label="Close">
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="dialog-form">
          {error && <ErrorMessage message={error} onClose={() => setError('')} />}

          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              {...register('username', {
                required: 'Username is required',
                minLength: { value: 3, message: 'Username must be at least 3 characters' }
              })}
              className="form-input"
              placeholder="Choose a username"
            />
            {errors.username && (
              <span className="form-error">{errors.username.message}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              {...register('password', {
                required: 'Password is required',
                validate: (value) => {
                  const validation = validatePassword(value);
                  return isPasswordValid(validation) || getPasswordErrorMessage(validation);
                }
              })}
              className="form-input"
              placeholder="Create a password"
            />
            {password && (
              <div className="password-requirements">
                <div className={passwordValidation.minLength ? 'req-met' : 'req-unmet'}>
                  {passwordValidation.minLength ? '✓' : '○'} At least 5 characters
                </div>
                <div className={passwordValidation.hasSpecialChar ? 'req-met' : 'req-unmet'}>
                  {passwordValidation.hasSpecialChar ? '✓' : '○'} 1 special character
                </div>
                <div className={passwordValidation.hasNumber ? 'req-met' : 'req-unmet'}>
                  {passwordValidation.hasNumber ? '✓' : '○'} 1 number
                </div>
                <div className={passwordValidation.hasLetter ? 'req-met' : 'req-unmet'}>
                  {passwordValidation.hasLetter ? '✓' : '○'} 1 letter
                </div>
              </div>
            )}
            {errors.password && (
              <span className="form-error">{errors.password.message}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="repeatPassword">Repeat Password</label>
            <input
              id="repeatPassword"
              type="password"
              {...register('repeatPassword', {
                required: 'Please repeat your password',
                validate: (value) => value === password || 'Passwords do not match'
              })}
              className="form-input"
              placeholder="Repeat your password"
            />
            {errors.repeatPassword && (
              <span className="form-error">{errors.repeatPassword.message}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="birthdate">Birthdate</label>
            <input
              id="birthdate"
              type="date"
              {...register('birthdate', {
                required: 'Birthdate is required'
              })}
              className="form-input"
            />
            {errors.birthdate && (
              <span className="form-error">{errors.birthdate.message}</span>
            )}
            <span className="form-hint">You must be at least 18 years old</span>
          </div>

          <Button type="submit" fullWidth disabled={isLoading}>
            {isLoading ? 'Creating account...' : 'Sign Up'}
          </Button>

          <div className="dialog-footer">
            <p>
              Already have an account?{' '}
              <button
                type="button"
                className="link-button"
                onClick={() => {
                  handleClose();
                  onSwitchToLogin();
                }}
              >
                Log in
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
}
